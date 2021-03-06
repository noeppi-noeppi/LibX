package io.github.noeppi_noeppi.libx.annotation.processor.modinit.codec;

import io.github.noeppi_noeppi.libx.annotation.Lookup;
import io.github.noeppi_noeppi.libx.annotation.processor.modinit.GeneratedCodec;
import io.github.noeppi_noeppi.libx.annotation.processor.modinit.ModEnv;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.*;

public class RegistryType implements CodecType {
    
    public static final String REGISTRY_TYPE = "net.minecraft.util.registry.Registry";
    // When something is added here, also add it to ProcessorInterface.getCodecDefaultRegistryKey
    public static final Set<String> ALLOWED_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "net.minecraft.world.biome.Biome",
            "net.minecraft.world.gen.DimensionSettings"
    )));
    
    @Override
    public boolean matchesDirect(VariableElement param, String name, ModEnv env) {
        return param.getAnnotation(Lookup.class) != null;
    }

    @Override
    public boolean matches(VariableElement param, String name, ModEnv env) {
        Element element = env.types().asElement(param.asType());
        if (element instanceof TypeElement) {
            return ((TypeElement) element).getQualifiedName().contentEquals(REGISTRY_TYPE);
        } else {
            return false;
        }
    }

    @Override
    public GeneratedCodec.CodecElement generate(VariableElement param, String name, GetterSupplier getter, ModEnv env) throws FailureException {
        String typeFqn = param.asType().toString();
        String typeFqnBoxed = env.boxed(param.asType()).toString();
        String namespace;
        String path;
        Lookup lookup = param.getAnnotation(Lookup.class);
        if (lookup == null) {
            namespace = "minecraft";
            path = null;
        } else {
            namespace = lookup.namespace();
            path = lookup.value().isEmpty() ? null : lookup.value();
        }
        TypeMirror mirror = param.asType();
        TypeElement generic = null;
        if (mirror instanceof DeclaredType) {
            List<? extends TypeMirror> generics = ((DeclaredType) mirror).getTypeArguments();
            if (generics != null && generics.size() == 1) {
                Element elem = env.types().asElement(generics.get(0));
                if (elem instanceof TypeElement) {
                    generic = (TypeElement) elem;
                }
            }
        }
        if (generic == null) {
            env.messager().printMessage(Diagnostic.Kind.ERROR, "Could not infer registry type for registry codec.", param);
            throw new FailureException();
        }
        if (path == null && !ALLOWED_TYPES.contains(generic.getQualifiedName().toString())) {
            env.messager().printMessage(Diagnostic.Kind.ERROR, "Can't infer registry key for type '" + generic.getQualifiedName() + "'. Set it by annotation value.", param);
            throw new FailureException();
        }
        return new GeneratedCodec.CodecRegistry(typeFqn, typeFqnBoxed, path == null ? null : namespace, path, generic.getQualifiedName().toString(), getter.get());
    }
}
