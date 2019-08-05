import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

public class Tree4j {

    private static FileFilter filter ;

    class JavaOrDirFilter implements FileFilter {
        private Pattern pattern ;

        JavaOrDirFilter(String regex){
            pattern = Pattern.compile(regex);
        }

        @Override
        public boolean accept(File file) {
            if(file.isDirectory()){
                return true;
            }else {
                return pattern.matcher(file.getPath()).matches();
            }
        }
    }

    Tree4j(){
        String regex = ".*";
        filter = new JavaOrDirFilter(regex);
    }

    public void accept(File currentFile) {
        accept(currentFile, filter);
    }

    public void accept(File currentFile, FileFilter filter) {

        File[] files = currentFile.listFiles(filter);

        if(files == null || files.length == 0 ){
            return ;
        }

        for (File file : files) {
            if(file.isDirectory()){
                accept(file);
            } else {
                fileOperate(file);
            }

        }
    }

    /**
     * 打印静态字段 将文件内容存储到...redis吧
     * @param file 具体 文件
     */
    private void fileOperate(File file) {
        System.out.println("fileName: [" + file.getName()  + "]");

        RedisPool.getJedis().incr(file.getName());
    }


    public static void main(String[] args) {
        new Tree4j().accept(new File("/Volumes/铛个里个铛铛铛"));
//        RedisPool.getJedis().incr("Collective Hindsight (Book 1) - Aaron Rosenberg.mobi");
    }

}
