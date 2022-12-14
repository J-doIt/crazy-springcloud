package com.crazymaker.springcloud.demo.controller;

import com.crazymaker.springcloud.standard.context.SpringContextUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/freemark/demo/")
@Api(tags = "Freemark Demo 演示")
public class FreemarkController
{

    @RequestMapping("/")
    public String index(Model model) {
        Map map = new LinkedHashMap<>();
        for (int i = 0; i < 5; i++) {
            map.put("key" + i, "value" + i);
        }
        model.addAttribute("list", Arrays.asList("string1", "string2", "string3", "string4", "string5", "string6"));
        model.addAttribute("map", map);
        model.addAttribute("name", "   htTps://wWw.zHyD.mE   ");
        model.addAttribute("htmlText", "<span style=\"color: red;font-size: 16px;\">html内容</span>");
        model.addAttribute("num", 123.012);
        model.addAttribute("null", null);
        model.addAttribute("dateObj", new Date());
        model.addAttribute("bol", true);
        return "index";
    }

    @RequestMapping("/createHtml")
    @ResponseBody
    public String createHtml(Model model){
        Map map = new LinkedHashMap<>();
        for (int i = 0; i < 5; i++) {
            map.put("key" + i, "value" + i);
        }
        model.addAttribute("list", Arrays.asList("string1", "string2", "string3", "string4", "string5", "string6"));
        model.addAttribute("map", map);
        model.addAttribute("name", "   htTps://wWw.zHyD.mE   ");
        model.addAttribute("htmlText", "<span style=\"color: red;font-size: 16px;\">html内容</span>");
        model.addAttribute("num", 123.012);
        model.addAttribute("null", null);
        model.addAttribute("dateObj", new Date());
        model.addAttribute("bol", true);
        return FreemarkerUtil.parseTpl("index", model.asMap());
    }

    static class FreemarkerUtil {

        public static String parseTpl(String viewName, Map<String, Object> params) {
            Configuration cfg = SpringContextUtil.getBean(Configuration.class);
            String html = null;
            Template t = null;
            try {
                t = cfg.getTemplate(viewName + ".ftl");
                html = processTemplateIntoString(t, params);
            } catch (IOException | TemplateException e) {
                e.printStackTrace();
            }
            return html;
        }

        public static String processTemplateIntoString(Template template, Object model) throws IOException, TemplateException
        {
            StringWriter result = new StringWriter();
            template.process(model, result);
            return result.toString();
        }
    }

}
