import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Tree4j {

    String[] fileTypeStrs = new String[]{"BMP","CHM","Cab","GIF","HTM","ISO","JPG","Lion","MF","MOBI","MRK","PDB","PDF","PNG","RAR","SnowLeopard","TXT","Txt","asc","azw","azw3","bin","bmp","caj","chm","cif","cip","class","crdownload","created","css","csv","dat","db","directoryStoreFile","doc","docx","donotpresent","downloading","dropbox","epub","evt","exe","flv","gif","hhc","htm","html","ico","indexArrays","indexBigDates","indexCompactDirectory","indexDirectory","indexGroups","indexHead","indexIds","indexPositionTable","indexPositions","indexPostings","indexTermIds","indexUpdates","ini","iso","jpg","json","kll","lnk","loc","mbp","md","mid","mobi","modified","msi","opf","pdf","pdg","plist","png","ppt","prc","rar","rtf","sgdownload","shadow","shadowIndexArrays","shadowIndexCompactDirectory","shadowIndexDirectory","shadowIndexGroups","shadowIndexHead","shadowIndexPositionTable","shadowIndexTermIds","shtml","state","svg","tar","thm","tmp","torrent","txt","updates","url","wav","wpt","xls","xlsx","xml","zip"};

    Set fileType;

    private static FileFilter filter ;

    String newFileDir = "/Volumes/铛个里个铛铛铛/byType/";

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
        fileType = new HashSet<String>(Arrays.asList(fileTypeStrs));
    }

    public void accept(File currentFile) throws IOException {
        accept(currentFile, filter);
    }

    public void accept(File currentFile, FileFilter filter) throws IOException {

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
    private void fileOperate(File file) throws IOException {
        //System.out.println("fileName: [" + file.getName()  + "]");

        //V1 查看全部文件
//        RedisPool.getJedis().incr(file.getName());

        //将文件分类
        String fileName = file.getName();
        String[] fileNameS = fileName.split("\\.");
        if(fileNameS.length <= 1) return;
        String fileNameEnd = fileNameS[fileNameS.length - 1];


//        System.out.println( "file path: " + file.getPath() + file.getName());
        if(fileType.contains(fileNameEnd)){
            String shell = "mv "
                    + file.getPath()
                    /*处理shell 通配符*/
                    .replaceAll("\\*", "\\\\\\*")
                    .replaceAll("\\?", "\\\\\\?")
                    .replaceAll("\\]", "\\\\\\]")
                    .replaceAll("\\[", "\\\\\\[")
                    .replaceAll("!", "\\\\!")
                    .replaceAll("\\{", "\\\\\\{")
                    .replaceAll("\\}", "\\\\\\}")
                    /* 处理shell 元字符*/
                    .replaceAll("=", "\\\\=")
                    .replaceAll("\\$", "\\\\\\$")
                    .replaceAll(">", "\\\\>")
                    .replaceAll("<", "\\\\<")
                    .replaceAll("\\|", "\\\\\\|")
                    .replaceAll("&", "\\\\&")
                    .replaceAll(";", "\\\\;")
                    .replaceAll("~", "\\\\~")
                    .replaceAll("\\)", "\\\\\\)")
                    .replaceAll(" ", "\\\\ ")
                    .replaceAll("\\(", "\\\\\\(")
                    /*转义符*/
                    .replaceAll("'", "\\\\'")
                    .replaceAll("\"", "\\\\\"")
                    .replaceAll("`", "\\\\`")

                    + " " + newFileDir + fileNameEnd;
            System.out.println(shell);
            runShell(shell);
        }else {
            System.out.println("没有归宿的文件：" + fileName);
        }


    }


    public static void main(String[] args) throws IOException {
        new Tree4j().accept(new File("/Volumes/铛个里个铛铛铛/kindle"));


//        System.out.println("sdssdsd*www.cs".replaceAll("\\*", "\\\\\\*"));
//        runShell( "ls");

    }

    private static void runShell( String shell) throws IOException {
        String[] cmd = new String[]{"/bin/sh", "-c", shell};
        Process ps = Runtime.getRuntime().exec(cmd);

        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        String result = sb.toString();

        System.out.println(result);
    }

}
