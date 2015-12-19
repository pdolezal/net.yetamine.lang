package net.yetamine.lang.creational;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import net.yetamine.lang.Quoting;
import net.yetamine.lang.functional.Single;

/**
 * A base class for non-trivial singletons (and possibly multitons) that can't
 * exploit the nature of enum constants while have to support serialization in a
 * way (deserialization should resolve to the "local" existing instance).
 */
public abstract class Singleton implements Serializable {

    /**
     * Annotates static methods that shall be used as the access points to get
     * an instance of a singleton.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(value = { ElementType.METHOD })
    public @interface AccessPoint {

        /**
         * Returns the distinguishing identifier of the access point for the
         * cases when a class provides more access points (for different
         * singletons). The identifier should not be {@code null}.
         *
         * @return the distinguishing access point identifier
         */
        String value() default "";
    }

    /**
     * Prepares a new instance.
     */
    protected Singleton() {
        // Default constructor
    }

    // Serialization support

    /** Serialization version: 1 */
    private static final long serialVersionUID = 1L;

    /**
     * Prevents an instance from incorrect deserialization.
     *
     * @param is
     *            the input stream. It must not be {@code null}.
     *
     * @throws InvalidObjectException
     *             thrown always as this method should never be invoked
     */
    protected final void readObject(ObjectInputStream is) throws InvalidObjectException {
        /* FindBugs warning (SE_METHOD_MUST_BE_PRIVATE): ignore
         *
         * No inherited class may allow direct deserialization. This can be
         * enforced by making this class protected and final, although this is
         * a very unusual declaration which would be wrong in all other cases.
         */
        throw new InvalidObjectException("Serialization proxy required.");
    }

    /**
     * Makes the serialization proxy.
     *
     * <p>
     * The provided implementation of this method makes a serialization proxy
     * instance which resolves the singleton then via the default access point
     * of the class itself. Inherited classes may override this method if they
     * need a different deserialization policy.
     *
     * @return the serialization proxy
     *
     * @throws ObjectStreamException
     *             if an inherited implementation needs to throw an exception
     *
     * @see Serializable
     */
    protected Object writeReplace() throws ObjectStreamException {
        return new SerializationProxy(getClass());
    }

    /**
     * Serialization proxy for singletons.
     */
    public static final class SerializationProxy implements Serializable {

        /** Class providing the singleton via an access point. */
        private final Class<?> singletonProvider;
        /** Class of the singleton type. */
        private final Class<?> singletonClass;
        /** Identifier of the access point. */
        private final String singletonIdentifier;

        /**
         * Creates a new instance that would use the default access point in the
         * singleton's class.
         *
         * @param singletonType
         *            the class of the singleton which provides the singleton's
         *            access point as well. It must not be {@code null}.
         */
        public SerializationProxy(Class<?> singletonType) {
            this(singletonType, singletonType, "");
        }

        /**
         * Create a new serialization proxy.
         *
         * @param providerClass
         *            the class which provides the singleton's access point. It
         *            must not be {@code null}.
         * @param singletonType
         *            the type of the singleton. It must not be {@code null}.
         * @param identifier
         *            the identifier of the access point. It must not be
         *            {@code null}.
         */
        public SerializationProxy(Class<?> providerClass, Class<?> singletonType, String identifier) {
            singletonProvider = Objects.requireNonNull(providerClass);
            singletonIdentifier = Objects.requireNonNull(identifier);
            singletonClass = Objects.requireNonNull(singletonType);
        }

        // Serialization support

        /** Serialization version: 1 */
        private static final long serialVersionUID = 1L;

        /**
         * Returns the resolved instance using the local access point.
         *
         * @return the resolved instance
         *
         * @see Serializable
         */
        private Object readResolve() {
            return Instance.lookup(singletonProvider, singletonClass, singletonIdentifier);
        }
    }

    /**
     * Provides utilities for using singleton access points.
     */
    public static final class Instance {

        /**
         * Returns the instance of a singleton using its default access point.
         *
         * @param <T>
         *            the type of the class
         * @param clazz
         *            the class whose single instance shall be found. It must
         *            not be {@code null} and it must declare an access point
         *            properly.
         *
         * @return the result of the access point
         *
         * @throws IllegalArgumentException
         *             if the class provides no default access point.
         * @throws ClassCastException
         *             if the found access point returns an incompatible
         *             instance
         */
        public static <T> T lookup(Class<? extends T> clazz) {
            return lookup(clazz, clazz, "");
        }

        /**
         * Returns the instance of a singleton using the given provider.
         *
         * @param <T>
         *            the type of the class
         * @param provider
         *            the class providing the access point. It must not be
         *            {@code null}.
         * @param type
         *            the type of the instance to be be found. It must not be
         *            {@code null}.
         * @param identifier
         *            the identifier of the access point. It must not be
         *            {@code null}.
         *
         * @return the result of the access point
         *
         * @throws IllegalArgumentException
         *             if the class provides no default access point.
         * @throws ClassCastException
         *             if the found access point returns an incompatible
         *             instance
         */
        public static <T> T lookup(Class<?> provider, Class<? extends T> type, String identifier) {
            Objects.requireNonNull(type);

            final Method accessPoint = locate(provider, identifier).orElseThrow(() -> {
                final String f = "Unable to resolve singleton access point with identifier %1$s in class %2$s.";
                final String m = String.format(f, Quoting.single(identifier), provider.getTypeName());
                return new IllegalArgumentException(m);
            });

            try {
                return type.cast(accessPoint.invoke(null));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Lookups the singleton access point with the given identifier.
         *
         * @param provider
         *            the class inspected for the access point method. It must
         *            not be {@code null}.
         * @param identifier
         *            the access point identifier. It must not be {@code null},
         *            an empty string requests the default access point.
         *
         * @return an {@link Optional} with the found method, or an empty
         *         instance if no single method found (either zero or more than
         *         one access point with the identifier has been found)
         */
        public static Optional<Method> locate(Class<?> provider, String identifier) {
            Objects.requireNonNull(identifier);
            final Predicate<Method> matching = m -> identifier.equals(m.getAnnotation(AccessPoint.class).value());
            return Single.head(locate(provider).filter(matching)).optional();
        }

        /**
         * Lookups all singleton access points in the given class.
         *
         * @param provider
         *            the class inspected for the access point method. It must
         *            not be {@code null}.
         *
         * @return the stream of access points
         */
        public static Stream<Method> locate(Class<?> provider) {
            return Stream.of(provider.getDeclaredMethods()).filter(m -> Modifier.isStatic(m.getModifiers()))
                    .filter(m -> m.getParameterCount() == 0).filter(m -> m.getReturnType() != Void.TYPE)
                    .filter(m -> m.isAnnotationPresent(AccessPoint.class));
        }

        private Instance() {
            throw new AssertionError();
        }
    }
}
