package bin.framework.servlet;

import bin.framework.classloader.BinUrlClassLoader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class BinHotServlet extends HttpServlet {


    private List<Object> controllerObjList;

    private BinUrlClassLoader binUrlClassLoader;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestURI = req.getRequestURI();

        if(requestURI.indexOf("binHotServlet/hot")!=-1){//只要是这个路径的就表示进行热换
            clearClsAndObj();
            loadObj();
            resp.getWriter().print("hot replace complete ! ! !");
        }

        String[] requestUriPathInfos = requestURI.split("/");
        if(requestUriPathInfos.length!=3){
            return;
        }
        String controllerStr = requestUriPathInfos[1];
        String controllerMethodStr = requestUriPathInfos[2];



        for (Object controllerObj :
                controllerObjList) {
            Class<?> controllerCls = controllerObj.getClass();
            String controllerClsName = controllerCls.getSimpleName();
            controllerClsName = toLowerCaseFirstOne(controllerClsName);
            if (controllerClsName.equals(controllerStr)) {
                try {
                    Method method = controllerCls.getMethod(controllerMethodStr);
                    Object obj = method.invoke(controllerObj);
                    String result = obj.toString();
                    resp.getWriter().print(result);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }



    @Override
    public void init() throws ServletException {
        super.init();
        loadObj();
    }

    /**
     * 通过自定义的ClassLoader进行加载项目的class文件
     */
    private void loadObj(){
        ServletConfig servletConfig = getServletConfig();
        String controllerArrStr = servletConfig.getInitParameter("controllerList");
        if (controllerArrStr == null) {
            throw new RuntimeException("no Controller set in controlleList !!!!");
        }
        ClassLoader classLoader = getClass().getClassLoader();
        binUrlClassLoader =new BinUrlClassLoader(classLoader);
        String[] controllerStrArr = controllerArrStr.split(",");
        controllerObjList = new LinkedList<>();
        for (int i = 0; i < controllerStrArr.length; i++) {
            String controllerStr = controllerStrArr[i];
            try {
                Class<?> controllerCls = binUrlClassLoader.loadClass(controllerStr);
                Object controllerObj = controllerCls.newInstance();
                controllerObjList.add(controllerObj);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 置空回收ClassLoader和controllerObjList
     */
    private void clearClsAndObj(){
        binUrlClassLoader=null;
        controllerObjList=null;
    }

    /**
     * 转换类的首字母为小写
     */
    private String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }


}
