package hk.ust.cse.model;

import hk.ust.cse.util.ReflectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingSupplier;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExtraLifeTest {

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Constructors")
    void testConstructors() {
        final var clazz = ExtraLife.class;
        final var ctors = ReflectionUtils.getPublicConstructors(clazz);

        assertEquals(2, ctors.length);

        assertDoesNotThrow((ThrowingSupplier<Constructor<ExtraLife>>) clazz::getConstructor);
        assertDoesNotThrow(() -> clazz.getConstructor(EntityCell.class));
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Methods")
    void testPublicMethods() {
        final var clazz = ExtraLife.class;
        final var publicMethods = ReflectionUtils.getPublicInstanceMethods(clazz);

        assertEquals(3, publicMethods.length);

        assertDoesNotThrow(() -> clazz.getDeclaredMethod("toUnicodeChar"));
        assertDoesNotThrow(() -> clazz.getDeclaredMethod("toASCIIChar"));
    }

    @Test
    @Tag("sanity")
    @DisplayName("Sanity Test - Public Fields")
    void testPublicFields() {
        final var clazz = ExtraLife.class;
        final var publicFields = ReflectionUtils.getPublicInstanceFields(clazz);

        assertEquals(0, publicFields.length);
    }
}
