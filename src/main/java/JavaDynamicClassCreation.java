import sun.misc.Unsafe;


import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;

import static java.util.Collections.singletonList;
import static javax.tools.JavaFileObject.Kind.SOURCE;


public class JavaDynamicClassCreation {

    public static final void main(String... args) throws ClassNotFoundException, URISyntaxException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        new JavaDynamicClassCreation().dynamicClassCreation();
    }

    public void dynamicClassCreation() throws ClassNotFoundException, IllegalAccessException, InstantiationException, URISyntaxException, NoSuchFieldException {


        final String fullName = "DynamicClassCreation";


        final StringBuilder source = new StringBuilder();
        source.append("public class DynamicClassCreation {\n");
        source.append(" public String toString() {\n");
        source.append(" return \"This was written by Rob Austin\";");
        source.append(" }\n");
        source.append("}\n");

        System.out.println(source);

        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        final SimpleJavaFileObject simpleJavaFileObject = new SimpleJavaFileObject(URI.create("string:///" + fullName.replace('.', '/')
                + SOURCE.extension), SOURCE) {

            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return source;
            }

            @Override
            public OutputStream openOutputStream() throws IOException {
                return byteArrayOutputStream;
            }
        };

        final JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        final JavaFileManager javaFileManager = new
                ForwardingJavaFileManager(javaCompiler
                        .getStandardFileManager(null, null, null)) {

                    @Override
                    public JavaFileObject getJavaFileForOutput(Location location,
                                                               String className,
                                                               JavaFileObject.Kind kind,
                                                               FileObject sibling) throws IOException {
                        return simpleJavaFileObject;
                    }
                };

        javaCompiler.getTask(null, javaFileManager, null, null, null, singletonList(simpleJavaFileObject)).call();
        final byte[] bytes = byteArrayOutputStream.toByteArray();

        final Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        final Unsafe unsafe = (Unsafe) f.get(null);

        final Class aClass = unsafe.defineClass(fullName, bytes, 0, bytes.length);

        final Object o = aClass.newInstance();
        System.out.println(o);


    }

}
