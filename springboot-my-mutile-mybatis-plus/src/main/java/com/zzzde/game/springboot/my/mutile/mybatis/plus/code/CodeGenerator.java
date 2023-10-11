package com.zzzde.game.springboot.my.mutile.mybatis.plus.code;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 */
@Slf4j
public class CodeGenerator {

    public static void main(String[] args) {
        //要生成表
        List<String> tables = new ArrayList<>();
        tables.add("bas_user");
        //表前缀过滤
        String [] tablePrefix =new String[]{"bas_"};
        //mapper对应库相关配置
        String mapperPackage = "bas";
        String customSuperMapperClass = "BasMapper";


        DataSourceConfig dataSourceConfig = new DataSourceConfig
                .Builder(
                "jdbc:mysql://localhost:3306/demo?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useSSL=false&serverTimezone=GMT%2B8&allowPublicKeyRetrieval=true",
                "root",
                "root")
                .dbQuery(new MySqlQuery())
                .typeConvert(new EasyMySqlTypeConvert())
                .keyWordsHandler(new MySqlKeyWordsHandler())
                .build();

        GlobalConfig globalConfig = new GlobalConfig
                .Builder()
                .author("zzzde") //设置作者
                .commentDate("yyyy-MM-dd HH:mm:ss")//注释日期
                .outputDir(System.getProperty("user.dir") + "/springboot-my-mutile-mybatis-plus/src/main/java")    //输出路径(写到java目录)
                //.fileOverride()  //开启覆盖之前生成的文件
                .disableOpenDir()
                .build();


        StrategyConfig strategyConfig = new StrategyConfig
                .Builder()
                .addInclude(tables)
                .addTablePrefix(tablePrefix)
                .entityBuilder().formatFileName("%s").enableLombok()
                .mapperBuilder().formatMapperFileName("%sMapper").formatXmlFileName("%sMapper")
                .controllerBuilder().formatFileName("%sController")
                .serviceBuilder().formatServiceFileName("%sService").formatServiceImplFileName("%sServiceImpl")
                .build();

        Map<OutputFile, String> pathInfo = new HashMap<>();
        pathInfo.put(OutputFile.mapperXml, System.getProperty("user.dir") + "/springboot-my-mutile-mybatis-plus/src/main/resources/mapper/" + mapperPackage);

        PackageConfig packageConfig = new PackageConfig
                .Builder()
                .parent("")
                .entity("com.zzzde.game.springboot.my.mutile.mybatis.plus.entity."+mapperPackage)
                .service("com.zzzde.game.springboot.my.mutile.mybatis.plus.service")
                .serviceImpl("com.zzzde.game.springboot.my.mutile.mybatis.plus.service.impl")
                .mapper("com.zzzde.game.springboot.my.mutile.mybatis.plus.mapper."+mapperPackage)
                .pathInfo(pathInfo)
                .build();

        TemplateConfig templateConfig = new TemplateConfig
                .Builder()
                .disable(TemplateType.CONTROLLER) //禁用controller
                .entity("/templates/entity.java")
                .service("/templates/service.java")
                .serviceImpl("/templates/serviceImpl.java")
                .mapper("/templates/mapper.java")
                .mapperXml("/templates/mapper.xml")
                .build();

        Map<String, Object> customMap = new HashMap<>();
        customMap.put("customSuperMapperClass",customSuperMapperClass);
        customMap.put("mapperPackage",mapperPackage);
        customMap.put("customSuperMapperClassPackage","com.zzzde.game.springboot.my.mutile.mybatis.plus.config." + customSuperMapperClass);

        Map<String, String> customFile = new HashMap<>();
        customFile.put("Repository.java", "/templates/kefu.repository.java.ftl");

        InjectionConfig injectionConfig = new InjectionConfig
                .Builder()
                .customMap(customMap)
                .customFile(customFile)
                .build();

        AutoGenerator generator = new AutoGenerator(dataSourceConfig);
        generator.global(globalConfig);
        generator.strategy(strategyConfig);
        generator.packageInfo(packageConfig);
        generator.template(templateConfig);
        generator.injection(injectionConfig);
        generator.execute(new EnhanceFreemarkerTemplateEngine());

    }


    /**
     * 自定义转换器转换器 内部类
     *         目的将数据库表中定义的  tinyint 或 bit类型转变为 java Integer 类型
     */
    static class EasyMySqlTypeConvert extends MySqlTypeConvert {
        @Override
        public IColumnType processTypeConvert(GlobalConfig config, String fieldType) {
            IColumnType iColumnType = super.processTypeConvert(config, fieldType);
            if (fieldType.equals("tinyint(1)")) {
                iColumnType = DbColumnType.INTEGER;
            }
            if (fieldType.equals("bit(1)")) {
                iColumnType = DbColumnType.BYTE;
            }
            String t = fieldType.toUpperCase();
            if (t.contains("DATE") || t.contains("TIMESTAMP")) {
                iColumnType = DbColumnType.DATE;
            }
            return iColumnType;
        }
    }

    /**
     * 代码生成器支持自定义[DTO\VO等]模版
     */
    static final class EnhanceFreemarkerTemplateEngine extends FreemarkerTemplateEngine {
        @Override
        protected void outputCustomFile( Map<String, String> customFile, TableInfo tableInfo, Map<String, Object> objectMap) {
            String entityName = tableInfo.getEntityName();
            String otherPath = this.getPathInfo(OutputFile.other);
            customFile.forEach((key, value) -> {
                String fileName = String.format(otherPath + File.separator + entityName + "%s", key);
                this.outputFile(new File(fileName), objectMap, value);
            });
        }
    }

}
