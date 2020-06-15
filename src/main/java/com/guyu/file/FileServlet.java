package com.guyu.file;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.UUID;

public class FileServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //判断上传的文件是普通的表单还是带文件的表单
        if (!ServletFileUpload.isMultipartContent(req)) {
            return;//终止方法，直接返回，说明这是一个普通的表单，
        }
        //创建上传文件的保存路径，建议在WEB-INF目录下，用户无法直接访问更加安全。
        String uploadPath = this.getServletContext().getRealPath("/WEB-INF/upload");
        File uploadFile = new File(uploadPath);
        if (!uploadFile.exists()) {
            uploadFile.mkdir();//如果没有就创建这个目录
        }
        //缓存，临时文件
        //临时路径，假如文件超过预期的大小，我们就把他放在一个临时的文件中，过几天自动删除，或者提醒用户转为永久
        String tmpPath = this.getServletContext().getRealPath("/WEB-INF/tmp");
        File file = new File(tmpPath);
        if (!file.exists()) {
            file.mkdir();
        }

        /*处理上传文件，一般需要流来获取，我们可以使用req.getInputStream(),原生态的文件上传流获取
        但是我们都建议使用apache的文件上传组件来实现，common-fileupload,它依赖于commons-io组件
        servletfileupload负责处理上传的文件数据，并将表单中每一个输入项封装成一个fileitem对象
        所以，我们需要在进行解析工作前构造DiskFileItemFactory对象
        通过servletfileupload对象的构造方法或setFileItemFactory方法
         设置servletfileupload对象的fileitemFactory属性
         */
        try {
            //将一大串的三步进行封装
            //1.创建DiskFileItemFactory对象，处理上传的路径或大小限制
            DiskFileItemFactory factory = getDiskFileItemFactory(file);
            //2.获取ServletFileUpload
            ServletFileUpload upload = getServletFileUpload(factory);
            //3.处理上传的文件
            String msg = uploadParseRequest(upload, req, uploadPath);
            //最后servlet转发消息，重定向的话场景不太使用
//        req.sendRedirect();//这是重定向
            req.setAttribute("msg", msg);
            req.getRequestDispatcher("info.jsp").forward(req, resp);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }


    }


    //第一步
    public static DiskFileItemFactory getDiskFileItemFactory(File file) {
        //1.创建DiskFileItemFactory对象，处理上传的路径或大小限制
        DiskFileItemFactory factory = new DiskFileItemFactory();

        //通过这个工厂设置一个缓冲区，当上传文件大于这个缓冲区的时候，就把它放到临时文件中,这里可以不做设置
        factory.setSizeThreshold(1024 * 1024);//缓存区大小为1M
        factory.setRepository(file);//临时目录的保存目录，需要一个file
        return factory;
    }

    //第二步
    public static ServletFileUpload getServletFileUpload(DiskFileItemFactory factory) {
        //2.获取servletfileupload
        ServletFileUpload upload = new ServletFileUpload(factory);

        //下面的这些可有可无
        //监听文件的上传速度
        upload.setProgressListener(new ProgressListener() {
            //第一个参数代表已经读取道德文件大小
            //第二个参数代表文件大小

            public void update(long l, long l1, int i) {
                System.out.println("总大小：" + l + "已上传：" + l1 + "最后一个不太清除是什么参数" + i);
            }
        });
        //处理乱码问题
        upload.setHeaderEncoding("UTF-8");
        //设置单个文件的最大值
        upload.setFileSizeMax(1024 * 1024 * 10);//10M
        //设置总共能够上传文件的大小
        //1024*1024*10=10M
        upload.setSizeMax(1024 * 1024 * 10);//10M
        return upload;
    }

    //第三步
    public static String uploadParseRequest(ServletFileUpload upload, HttpServletRequest req, String uploadPath) throws FileUploadException, IOException {
        String msg = "";
        //3.处理上传文件
        //把前端请求解析，封装成一个fileitem对象，需要从ServletFileUpload对象中获取
        List<FileItem> fileItems = upload.parseRequest(req);//会抛出异常
        //进行遍历
        for (FileItem fileItem : fileItems) {
            //判断上传的文件是普通的表单，还是带文件的表单
            if (fileItem.isFormField()) {//普通的
                String name = fileItem.getFieldName();
                String value = fileItem.getString("UTF-8");//处理乱码
                System.out.println(name + ":" + value);
            } else {//文件的情况下
                //=====================处理文件===================
                String uploadFileName = fileItem.getName();
                System.out.println("上传的文件名" + uploadFileName);
                //可能存在文件名不合法的情况,trim()去除文件名前后的空格
                if (uploadFileName.trim().equals("") || uploadFileName == null) {
                    continue;
                }
                //获得上传文件的名字 例如路径为 /images/boy/guyu.jpg
                String fileName = uploadFileName.substring(uploadFileName.lastIndexOf("/") + 1);
                //获取文件名的后缀
                String fileExtName = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
                    /*
                    如果文件名后缀不是我们想要的，
                    就直接return，不处理，告诉用户文件类型不对
                     */
                System.out.println("文件信息【 件名" + fileName + "====文件类型" + fileExtName);
                //可以使用UUID（唯一识别的通用码）保证文件名唯一
                //UUID.randomUUID()，随机生成一个唯一识别的通用吗

                //网络传输中的东西，都需要序列化，
                //POJO ,实体类，如果想要在多个电脑上运行，传输===》需要把对象序列化了
                //JNI=Java native interface
                //implements Serializable :标记接口，JVM---》Java栈 本地方法栈 native --》c++
                String uuidPath = UUID.randomUUID().toString();

                //=====================存放地址===================
                //存到哪？uploadPath
                //文件真实存储的路径
                String realPath = uploadPath + uuidPath;
                //给每一个文件创建一个文件夹
                File realPathFile = new File(realPath);
                if (!realPathFile.exists()) {
                    realPathFile.mkdir();
                }
                System.out.println("真实的路径" + realPath);

                //=====================文件传输===================
                //这没啥说的记住就行
                //获得文件上传的流
                InputStream inputStream = fileItem.getInputStream();

                //创建一个文件输出流
                //realPath=真实的文件夹
                //差了一个文件，加上输出文件的名字+"/"+uuidFileName
                FileOutputStream fos = new FileOutputStream(realPath + "/" + fileName);

                //创建一个缓冲区
                byte[] buffer = new byte[1024 * 1024];

                //判断是否读取失败
                int len = 0;
                //如果大于0说明还存在数据
                while ((len = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                //关闭流
                fos.close();
                inputStream.close();

                msg = "文件上传成功";
                fileItem.delete();//文件上传成功，清除临时文件
            }


        }
        return msg;
    }
}
