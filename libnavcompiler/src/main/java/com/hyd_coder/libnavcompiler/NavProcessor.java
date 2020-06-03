package com.hyd_coder.libnavcompiler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.auto.service.AutoService;
import com.hyd_coder.libnavannotationa.ActivityDestination;
import com.hyd_coder.libnavannotationa.FragmentDestination;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Cerated by huangyingde
 * Create date : 2020/6/1 17:32
 * description : APP页面导航信息收集注解处理器
 *
 * AutoService注解：就这么一标记，annotationProcessor  make project应用一下,编译时就能自动执行该类了。
 *
 * SupportedSourceVersion注解:声明我们所支持的jdk版本
 *
 * SupportedAnnotationTypes:声明该注解处理器想要处理那些注解
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.hyd_coder.libnavannotationa.FragmentDestination", "com.hyd_coder.libnavannotationa.ActivityDestination"})
public class NavProcessor extends AbstractProcessor {

    private Messager mMessager;
    private Filer mFiler;
    private static final String OUTPUT_FILE_NAME = "destination.json";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        // 日志打印,在java环境下不能使用android.util.log.e()
        mMessager = processingEnv.getMessager();
        // 文件处理工具
        mFiler = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 通过处理器环境上下文roundEnv分别获取 项目中标记的FragmentDestination.class 和ActivityDestination.class注解。
        // 此目的就是为了收集项目中哪些类 被注解标记了
        Set<? extends Element> fragmentElements = roundEnv.getElementsAnnotatedWith(FragmentDestination.class);
        Set<? extends Element> ActivityElements = roundEnv.getElementsAnnotatedWith(ActivityDestination.class);

        if (!fragmentElements.isEmpty() || !ActivityElements.isEmpty()) {
            HashMap<String, JSONObject> destMap = new HashMap<>();
            //分别 处理FragmentDestination  和 ActivityDestination 注解类型
            //并收集到destMap 这个map中, 以此就能记录下所有的页面信息了
            handleDestination(fragmentElements, FragmentDestination.class, destMap);
            handleDestination(ActivityElements, ActivityDestination.class, destMap);

            // 将解析注解生成的destination.json文件写入到 /app/src/main/assets/ 目录下
            FileOutputStream fos = null;
            OutputStreamWriter writer = null;
            try {
                // filer.createResource()意思是创建源文件
                // 我们可以指定为class文件输出的地方，
                // StandardLocation.CLASS_OUTPUT：java文件生成class文件的位置，/app/build/intermediates/javac/debug/classes/目录下
                // StandardLocation.SOURCE_OUTPUT：java文件的位置，一般在/ppjoke/app/build/generated/source/apt/目录下
                // StandardLocation.CLASS_PATH 和 StandardLocation.SOURCE_PATH用的不多，指定了这个参数，就要指定生成文件的pkg包名了
                FileObject resource = mFiler.createResource(StandardLocation.CLASS_OUTPUT, "", OUTPUT_FILE_NAME);
                String resourcePath = resource.toUri().getPath();
                mMessager.printMessage(Diagnostic.Kind.NOTE, "resourcePath:" + resourcePath);

                // 由于我们想要把json文件生成在app/src/main/assets/目录下,所以这里可以对字符串做一个截取，
                // 以此便能准确获取项目在每个电脑上的 /app/src/main/assets/的路径
                String appPath = resourcePath.substring(0, resourcePath.indexOf("app") + 4);
                String assetsPath = appPath + "src/main/assets/";

                File file = new File(assetsPath);

                if (!file.exists()) {
                    file.mkdirs();
                }

                // 开始写入了
                File outputFile = new File(file, OUTPUT_FILE_NAME);
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                outputFile.createNewFile();

                // 利用fastJson把收集到的所有的页面信息 转换成JSON格式的。并输出到文件中
                String content = JSON.toJSONString(destMap);
                fos = new FileOutputStream(outputFile);
                writer = new OutputStreamWriter(fos);
                writer.write(content);
                writer.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return true;
    }

    private void handleDestination(Set<? extends Element> elements, Class<? extends Annotation> annotationClazz, HashMap<String, JSONObject> destMap) {
        for (Element element : elements) {
            // TypeElement是Element的一种。
            // 如果我们的注解标记在了类名上。所以可以直接强转一下, 使用它得到全类名
            TypeElement typeElement = (TypeElement) element;
            String clazzName = typeElement.getQualifiedName().toString();
            // 页面的id.此处不能重复,使用页面的类名的hasCode的绝对值即可
            int id = Math.abs(clazzName.hashCode());
            // 页Url相当于隐式跳转意图中的host://schem/path格式
            String pageUrl = null;
            // 是否需要登录
            boolean needLogin = false;
            // 是否作为首页的第一个展示的页面
            boolean asStarter = false;
            // 标记该页面是fragment 还是activity类型的
            boolean isFragment = false;

            Annotation annotation = element.getAnnotation(annotationClazz);
            if (annotation instanceof FragmentDestination) {
                FragmentDestination fragmentDestination = (FragmentDestination) annotation;
                pageUrl = fragmentDestination.pageUrl();
                needLogin = fragmentDestination.needLogin();
                asStarter = fragmentDestination.asStarter();
                isFragment = true;
            } else if (annotation instanceof ActivityDestination) {
                ActivityDestination fragmentDestination = (ActivityDestination) annotation;
                pageUrl = fragmentDestination.pageUrl();
                needLogin = fragmentDestination.needLogin();
                asStarter = fragmentDestination.asStarter();
                isFragment = false;
            }

            if (destMap.containsKey(pageUrl)) {
                mMessager.printMessage(Diagnostic.Kind.ERROR, "不同的页面不允许使用相同的pageUrl：" + clazzName);
            } else {
                JSONObject object = new JSONObject();
                object.put("id", id);
                object.put("needLogin", needLogin);
                object.put("asStarter", asStarter);
                object.put("pageUrl", pageUrl);
                object.put("className", clazzName);
                object.put("isFragment", isFragment);
                destMap.put(pageUrl, object);
            }
        }
    }
}
