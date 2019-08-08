import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class Tree4j {

    String[] fileTypeStrs = new String[]{"BMP","CHM","Cab","GIF","HTM","ISO","JPG","Lion","MF","MOBI","MRK","PDB","PDF","PNG","RAR","SnowLeopard","TXT","Txt","asc","azw","azw3","bin","bmp","caj","chm","cif","cip","class","crdownload","created","css","csv","dat","db","directoryStoreFile","doc","docx","donotpresent","downloading","dropbox","epub","evt","exe","flv","gif","hhc","htm","html","ico","indexArrays","indexBigDates","indexCompactDirectory","indexDirectory","indexGroups","indexHead","indexIds","indexPositionTable","indexPositions","indexPostings","indexTermIds","indexUpdates","ini","iso","jpg","json","kll","lnk","loc","mbp","md","mid","mobi","modified","msi","opf","pdf","pdg","plist","png","ppt","prc","rar","rtf","sgdownload","shadow","shadowIndexArrays","shadowIndexCompactDirectory","shadowIndexDirectory","shadowIndexGroups","shadowIndexHead","shadowIndexPositionTable","shadowIndexTermIds","shtml","state","svg","tar","thm","tmp","torrent","txt","updates","url","wav","wpt","xls","xlsx","xml","zip"};

    Set fileType;

    private static FileFilter filter ;

    String targetFileDir = "/Volumes/铛个里个铛铛铛/byType/";


    /**
     * 打印静态字段 将文件内容存储到...redis吧
     * @param file 具体 文件
     */
    private void fileOperate(File file) throws IOException {

        //V1 查看全部文件
//        RedisPool.getJedis().incr(file.getName());

        //V2 根据文件类型分类。
//        sortFileByFileType(fidle);

        //V3 根据文件分类 如果文件存在 判断md5值 如果md5存在则删除原来的文件
        sortFileByFileTypeAndMD5(file);

    }

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
        fileType = new HashSet<>(Arrays.asList(fileTypeStrs));
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



    private void sortFileByFileTypeAndMD5(File file) throws IOException{

        System.out.println("处理文件：" + file.getName());
        String fileNameSuffix = getFileSuffix(file.getName());
        if(fileNameSuffix == null) return;
        if(!fileType.contains(fileNameSuffix)) return;

        File target = new File(targetFileDir + fileNameSuffix + "/" + file.getName() );

        if(!target.exists()) mv( file.getPath(), fileNameSuffix);

        if(md5(target.getPath()).equals( md5(file.getPath())) ){
            file.delete();
        }else {
            mv(file.getPath(),
                    fileNameSuffix + "/"+ randomName(fileNameSuffix, file.getName()));
        }

    }

    private static String randomName(String fileNameSuffix, String fileName) {
        return fileName.replace("." + fileNameSuffix,   new Random().nextInt(1000) + "." + fileNameSuffix );
    }

    private String md5(String path) throws IOException {
        //md5 ~/tmp ~/tmp1 | sed "s/^.*= //g"
        return runShell("md5 " + formatStringInOrderToShellCanUse(path) + " | sed \"s/^.*= //g\" ");
    }

    private void sortFileByFileType(File file) throws IOException {

        String fileNameSuffix = getFileSuffix(file.getName());
        if(fileNameSuffix == null) return;

        if(fileType.contains(fileNameSuffix)){
            mv(file.getPath(), fileNameSuffix);
        }
    }

    private void mv(String sourcePath, String dirName) throws IOException {
        String shell = "mv " + formatStringInOrderToShellCanUse(sourcePath) + " " + targetFileDir + formatStringInOrderToShellCanUse(dirName);
        runShell(shell);
    }

    private String getFileSuffix(String fileName){
        String[] fileNameSnippet = fileName.split("\\.");
        if(fileNameSnippet.length <= 1) return null;
        return fileNameSnippet[fileNameSnippet.length - 1];
    }

    private static String formatStringInOrderToShellCanUse(String name) {
        return name
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
//        .replaceAll("-", "\\\\-")
        /*转义符*/
//        .replaceAll("'", "\\\\'")
        .replaceAll("\"", "\\\\\"")
        .replaceAll("`", "\\\\`");
    }


    public static void main(String[] args) throws IOException {
        new Tree4j().accept(new File("/Volumes/铛个里个铛铛铛/kindle"));

//        System.out.println(randomName("exe", "tmp.exe"));
//        runShell( "ls");

    }

    private static String runShell( String shell) throws IOException {
        System.out.println(shell);
        String[] cmd = new String[]{"/bin/sh", "-c", shell};
        Process ps = Runtime.getRuntime().exec(cmd);

        BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
        StringBuffer sb = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();

    }

}
