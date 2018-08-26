package bin.framework.classloader;

import java.io.*;

public class BinUrlClassLoader extends ClassLoader {

    private String baseDir;

    /**
     * 判断是否已经找到了BinUrlClassLoader的class文件，主要是为减少判断的性能消耗
     */
    private boolean isFindBinUrlClassLoaderClass = false;

    public BinUrlClassLoader(ClassLoader classLoader) {
        super(classLoader);
        File classPathFile = new File(BinUrlClassLoader.class.getResource("/").getPath());
        baseDir = classPathFile.toString();
        recursionClassFile(classPathFile);
    }

    /**
     * 遍历项目的class文件
     */
    private void recursionClassFile(File classPathFile) {
        if (classPathFile.isDirectory()) {
            File[] files = classPathFile.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                recursionClassFile(file);
            }
        } else if (classPathFile.getName().indexOf(".class") != -1) {
            getClassData(classPathFile);
        }
    }

    /**
     * 获取类数据
     */
    private void getClassData(File classPathFile) {
        try {
            if (!isFindBinUrlClassLoaderClass && classPathFile.getName().equals(BinUrlClassLoader.class.getSimpleName() + ".class")) {
                isFindBinUrlClassLoaderClass = true;
            } else {
                InputStream fin = new FileInputStream(classPathFile);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int bufferSize = 4096;
                byte[] buffer = new byte[bufferSize];
                int byteNumRead = 0;
                while ((byteNumRead = fin.read(buffer)) != -1) {
                    bos.write(buffer, 0, byteNumRead);
                }
                byte[] classBytes = bos.toByteArray();
                defineClass(getClassName(classPathFile), classBytes, 0, classBytes.length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取类文件
     */
    private String getClassName(File classPathFile) {
        String classPath = classPathFile.getPath();
        String packagePath = classPath.replace(baseDir, "");
        String className = packagePath.replace("\\", ".").substring(1);
        return className.replace(".class", "");
    }


    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class cls = null;
        cls = findLoadedClass(name);
        if (cls == null) {
//            cls = getSystemClassLoader().loadClass(name);
            System.out.println(getSystemClassLoader().toString());
            System.out.println(getParent());
            cls=getParent().loadClass(name);

        }
        if (cls == null) {
            throw new ClassNotFoundException(name);
        }
        if (resolve) {
            resolveClass(cls);
        }
        return cls;
    }

    public static void main(String[] args) {
        String classPtahStr = BinUrlClassLoader.class.getResource("").getPath();
        File file = new File(classPtahStr + BinUrlClassLoader.class.getSimpleName() + ".class");

        System.out.println(file.getName());

        String baseDir = new File(BinUrlClassLoader.class.getResource("/").getPath()).toString();

        System.out.println(baseDir);

        String filePath = file.toString();
        String packagePath = filePath.replace(baseDir, "");
        System.out.println(packagePath);


        String classPath = packagePath.replace("\\", ".").substring(1);
        System.out.println(classPath);
    }

}
