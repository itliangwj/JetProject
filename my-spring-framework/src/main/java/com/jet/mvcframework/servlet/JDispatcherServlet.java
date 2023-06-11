package com.jet.mvcframework.servlet;

import com.jet.mvcframework.annotation.JAutowired;
import com.jet.mvcframework.annotation.JController;
import com.jet.mvcframework.annotation.JRequestMapping;
import com.jet.mvcframework.annotation.JService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jet
 * @version 1.0
 * @description: TODO
 * @date 2023/6/4 8:23
 */
public class JDispatcherServlet extends HttpServlet {
    /***
     *和web.xml中param-name的值一致
     */
    public static final String LOCATION = "contextConfigLocation";

    /***
     *保存所有配置信息
     */
    private Properties p = new Properties();

    /***
     *保存所有被扫描到的相关类名
     */
    private List<String> classNames = new ArrayList<String>();

    /***
     *核心IOC容器，保存所有初始化的bean
     */
    private ConcurrentHashMap<String,Object> ioc = new ConcurrentHashMap<String,Object>();

    /***
     *保存所有url和方法的映射
     */
    private Map<String, Method> handlerMapping = new HashMap<String,Method>();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("==================>doPost");
        System.out.println(System.currentTimeMillis());
        try{
            doDispatch(req, resp);
        }catch (Exception e){
            resp.getWriter().write("500 Exception,Dateils:\r\n" + Arrays.toString(e.getStackTrace())
                    .replaceAll("\\[]|\\]","").replaceAll(",\\s","\r\n"));

        }
    }

    public void init(ServletConfig servletConfig) throws ServletException {
        /**
         * 加载配置文件
         * */
        doLoadConfig(servletConfig.getInitParameter(LOCATION));

        /**
         * 扫描所有相关的类
         * */
        doScanner(p.getProperty("scanPackage"));

        /**
         * 初始化所有相关类的实例，并保存到IOC容器中
         * */
        doInstance();

        /**
         * 依赖注入
         * */
        doAutowired();

        /**
         *构造handlerMapping
         * */
        initHandlerMapping();

        /**
         * 等待请求，匹配URL，定位方法，反射调用执行，调用doGet,doPost方法
         * */

        //提示信息
        System.out.println("framework is init");
    }
    public void doLoadConfig(String location){
        InputStream fis = null;
        try {
            fis = this.getClass().getClassLoader().getResourceAsStream(location);
            //读取配置文件
            p.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (fis != null){
                    fis.close();
                }
            }catch (Exception e1){
                e1.printStackTrace();
            }
        }
    }
    private void doScanner(String packageName){
        URL url = this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.","/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(packageName + "." + file.getName());
            }else {
                //格式：包名+.+类名
                classNames.add(packageName + "." + file.getName().replace(".class","").trim());
            }
        }
    }
    private void doInstance(){
        if (classNames.size() == 0){return;}
        try {
            for (String className :classNames){
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(JController.class)){
                    //默认将首字母小写作为beanName
                    String beanName = lowerFirstCase(clazz.getSimpleName());
                    ioc.put(beanName,clazz.newInstance());
                }else if (clazz.isAnnotationPresent(JService.class)){
                    JService service = clazz.getAnnotation(JService.class);
                    String beanName = service.value();
                    //如果用户自己设置了名字，使用用户自己的名字
                    if (!"".equals(beanName.trim())){
                        ioc.put(beanName,clazz.newInstance());
                        continue;
                    }
                    //如果自己没有设置名字，就按接口类型创建一个实例
                    Class<?>[] interfaces = clazz.getInterfaces();
                    ioc.put(lowerFirstCase(clazz.getSimpleName()),clazz.newInstance());
//                    for (Class<?> i : interfaces){
//                        ioc.put(i.getName(),clazz.newInstance());
//                    }
                }else {
                    continue;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    private void doAutowired(){
        if (ioc.isEmpty()){
            return;
        }

        for (Map.Entry<String,Object> entry : ioc.entrySet()){
            //拿到实例对象中的所有属性
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields){
                if (!field.isAnnotationPresent(JAutowired.class)){continue;}
                JAutowired autowired = field.getAnnotation(JAutowired.class);
                String beanName = autowired.value().trim();
                if ("".equals(beanName)){
                    beanName = lowerFirstCase(field.getType().getSimpleName());
                }
                field.setAccessible(true); //设置私有属性的访问权限

                try{
                    field.set(entry.getValue(), ioc.get(beanName));
                }catch (Exception e){
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }

    private void initHandlerMapping(){
        if (ioc.isEmpty()){return;
        }

        for (Map.Entry<String,Object> entry : ioc.entrySet()){
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(JController.class)){continue;}

            String baseUrl = "";
            //获取Controller中的url配置
            if (clazz.isAnnotationPresent(JRequestMapping.class)){
                JRequestMapping requestMapping = clazz.getAnnotation(JRequestMapping.class);
                baseUrl = requestMapping.value();
            }

            Method[] methods = clazz.getMethods();
            for (Method method : methods){
                if (!method.isAnnotationPresent(JRequestMapping.class)){continue;}

                //映射URL
                JRequestMapping requestMapping = method.getAnnotation(JRequestMapping.class);
                String url = (baseUrl + requestMapping.value().replaceAll("/+","/"));
                handlerMapping.put(url, method);
                System.out.println("mapped" + url + "," + method);
            }
        }
    }

    private void doDispatch(HttpServletRequest req,HttpServletResponse resp)throws Exception{
        if (this.handlerMapping.isEmpty()){return;}

        String url = req.getRequestURI();
        String contextpath = req.getContextPath();
        url = url.replace(contextpath,"").replaceAll("/+","/");

        if (!this.handlerMapping.containsKey(url)){
            resp.getWriter().write("404 Not Found!!!");
            return;
        }

        Map<String,String[]> params = req.getParameterMap();
        Method method = this.handlerMapping.get(url);
        //获取方法的参数列表
        Class<?>[] paramterTypes = method.getParameterTypes();
        //获取请求参数
        Map<String,String[]> paramterMap = req.getParameterMap();
        //保存参数值
        Object[] paramValues = new Object[paramterTypes.length];
        //方法的参数列表
        for (int i = 0; i < paramterTypes.length; i++){
            //根据参数名称，做某些处理
            Class paramterType = paramterTypes[i];
            if (paramterType == HttpServletRequest.class){
                paramValues[i] = req;
                continue;
            }else if (paramterType == HttpServletResponse.class){
                paramValues[i] = resp;
                continue;
            }else if (paramterType == String.class){
                for (Map.Entry<String,String[]> param : paramterMap.entrySet()){
                    String value = Arrays.toString((param.getValue()))
                            .replaceAll("\\[|\\]","")
                            .replaceAll(",\\s",",");
                    paramValues[i] = value;

                }
            }
        }

        try {
            String beanName = lowerFirstCase(method.getDeclaringClass().getSimpleName());
            //利用反射机制来调用
            method.invoke(this.ioc.get(beanName),paramValues);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
