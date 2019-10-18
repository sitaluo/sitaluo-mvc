package com.sitaluo.mvc.servlet;

import com.sitaluo.demo.controller.UserController;
import com.sitaluo.mvc.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 所有请求的入口
 * loadOnStartup = 1 在启动的时候加载
 */
@WebServlet(name = "dispatchServlet",urlPatterns = "/*",loadOnStartup = 1)
public class DispatchServlet extends HttpServlet {

    //扫描宝路径
    private String basePackage = "com.sitaluo";
    //存储扫描包下的类的全路径名，如：com.sitaluo.demo.controller.UserController
    private List<String> classAllNames = new ArrayList<String>();
    //实例化对象map
    private Map<String,Object> instanceMap = new HashMap<String, Object>();
    //请求路径：controller有requestMappring注解的方法
    private Map<String,Method> uri2MethodMap = new HashMap<String, Method>();
    private Map<String,String> uri2ControllerNameMap = new HashMap<String, String>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            scanBasePackage(basePackage);
            instance(classAllNames);
            ioc();
            handleUriMethodMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String requestPath = requestURI.replaceAll(contextPath,"");
        System.out.println("requestPath:"+requestPath);
        Method method = uri2MethodMap.get(requestPath);
        String controllerClassName = uri2ControllerNameMap.get(requestPath);

        UserController controller = (UserController) instanceMap.get(controllerClassName);
        try {
            method.setAccessible(true);
            method.invoke(controller);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }




        System.out.println("doPost..." + requestURI);
        if(requestURI.equals("/index")){
            req.getRequestDispatcher("/index.jsp").forward(req,resp);
        }else {
            req.getRequestDispatcher("/index.jsp").forward(req,resp);
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    private void scanBasePackage(String basePackage){
        System.out.println( "扫描："+basePackage);
        String packageFilePath = basePackage.replace(".", "/");
        URL url = this.getClass().getClassLoader().getResource(basePackage.replace(".", "/"));
        File basePackageFile = new File(url.getPath());
        File[] childFiles = basePackageFile.listFiles();
        for (File file : childFiles ) {
            if(file.isDirectory()){
                scanBasePackage(basePackage + "."+file.getName());
            }else if(file.isFile()){
                classAllNames.add(basePackage + "." + file.getName().split("\\.")[0]);
            }
        }
    }

    private void instance(List<String> classAllNames) throws Exception{
        for (String clazz: classAllNames ) {
            Class cls = Class.forName(clazz);
            if(cls.isAnnotationPresent(Controller.class) ){
                Controller annotation = (Controller)cls.getAnnotation(Controller.class);
                instanceMap.put(annotation.value(),cls.newInstance());
            }else if(cls.isAnnotationPresent(Service.class)){
                Service annotation = (Service)cls.getAnnotation(Service.class);
                instanceMap.put(annotation.value(),cls.newInstance());
            }else if(cls.isAnnotationPresent(Repository.class)){
                Repository annotation = (Repository)cls.getAnnotation(Repository.class);
                instanceMap.put(annotation.value(),cls.newInstance());
            }
        }
    }
    private void ioc() throws IllegalAccessException {
        for (Map.Entry<String,Object> entry: instanceMap.entrySet()){
            Field[] declaredFields = entry.getValue().getClass().getDeclaredFields();
            for (Field field: declaredFields ) {
                if(field.isAnnotationPresent(Qualifier.class)){
                    String name = field.getAnnotation(Qualifier.class).value();
                    field.setAccessible(true);
                    field.set(entry.getValue(),instanceMap.get(name));
                }
            }
        }
    }

    private void handleUriMethodMap() throws ClassNotFoundException {
        for (String clazzName : classAllNames) {
            Class clazz = Class.forName(clazzName);
            if(clazz.isAnnotationPresent(Controller.class)){
                Controller controller = (Controller) clazz.getAnnotation(Controller.class);
                String controllerName = controller.value();
                String baseUri = "";
                if(clazz.isAnnotationPresent(RequestMapping.class)){
                    RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
                    baseUri = requestMapping.value();
                }
                Method[] methods = clazz.getMethods();
                for (Method method: methods ) {
                    if(method.isAnnotationPresent(RequestMapping.class)){
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        baseUri = baseUri + requestMapping.value();
                        uri2MethodMap.put(baseUri,method);
                        uri2ControllerNameMap.put(baseUri,controllerName);
                    }

                }
            }
        }
    }


}
