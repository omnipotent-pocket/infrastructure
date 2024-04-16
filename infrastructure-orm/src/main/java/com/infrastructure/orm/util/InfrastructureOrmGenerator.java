package com.infrastructure.orm.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public class InfrastructureOrmGenerator {

    public static boolean isNumeric(String str){
        if (str.equals("0")){
            return true;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        boolean b = pattern.matcher(str).matches();
        return b && !str.startsWith("0");
    }

    /**
     * 根据md生成sql文件
     *
     * @param inputFileName  输入的md文件路径
     * @param outputFileName 输出的sql文件路径
     */
    public static void generateSql(String inputFileName, String outputFileName, String host, String port, String dbName, String user, String password, boolean autoCreate) {
        String os = System.getProperty("os.name");
        InputStream inputStream;
        String exeSuffix ;
        if(os.contains("Mac")){
            inputStream = InfrastructureOrmGenerator.class.getClassLoader().getResourceAsStream("auto_producer_mac.sh");
            exeSuffix = ".sh";
        } else if (os.contains("Windows")){
            inputStream = InfrastructureOrmGenerator.class.getClassLoader().getResourceAsStream("auto_producer_windows.exe");
            exeSuffix = ".exe";
        } else if (os.contains("Linux")){
            inputStream = InfrastructureOrmGenerator.class.getClassLoader().getResourceAsStream("auto_producer_linux.sh");
            exeSuffix = ".sh";
        } else {
            inputStream = null;
            exeSuffix = "";
        }
        if(inputStream == null){
            log.error("不支持当前操作系统，os:{}",os);
            return;
        }
        File tempFile = null;
        BufferedReader reader = null;
        BufferedOutputStream out = null;
        try {
            tempFile = File.createTempFile(System.getProperty("user.dir") + "/" +System.currentTimeMillis()+ IdUtil.fastSimpleUUID(), exeSuffix);
            out = FileUtil.getOutputStream(tempFile);
            IoUtil.copy(inputStream, out);
            tempFile.setExecutable(true);
            tempFile.setReadable(true);
            tempFile.setWritable(true);
            Process process = new ProcessBuilder(tempFile.getAbsolutePath(), inputFileName,outputFileName).start();

            // 读取命令输出
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            // 等待命令执行完毕
            int exitCode = process.waitFor();
            System.out.println("Command exited with code " + exitCode);
            if (autoCreate) {
                mybatisExec(outputFileName, host, port, dbName, user, password);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
                if(reader != null){
                    reader.close();
                }
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileUtil.del(tempFile);
        }
    }

    private static void mybatisExec(String sqlFile, String host, String port, String dbName, String user, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useUnicode=true&useSSL=false&characterEncoding=utf8";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        ScriptRunner runner = new ScriptRunner(conn);
        try {
            runner.setStopOnError(true);
            runner.runScript(new FileReader(sqlFile));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /**
     * 生成数据库实体
     *
     * @param outputPath  文件输出路径
     * @param author      注释作者
     * @param packagePath      注释作者
     * @param host        db的ip
     * @param port        db的端口
     * @param dbName      db的名字
     * @param user        数据库用户名
     * @param password    数据库密码
     * @param dbShortName 包名(多数据源时需要将不同db的表放在不同包下)
     * @param tables      需要生成的表数组
     */
    public static void generateOrm(String outputPath, String author,  String packagePath, String host, String port,
                                   String dbName, String user, String password, String dbShortName,
                                   String... tables) {
        AutoGenerator mpg = new AutoGenerator();
        if(StrUtil.isEmpty(packagePath)){
            packagePath = "infrastructure";
        }

        //全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setOutputDir(outputPath + "/src/main/java");
        gc.setFileOverride(true);
        gc.setOpen(false);
        gc.setAuthor(author);
        gc.setSwagger2(false);
        gc.setBaseResultMap(true);
        gc.setBaseColumnList(true);
        gc.setIdType(IdType.AUTO);
        gc.setDateType(DateType.ONLY_DATE);
        gc.setEntityName(null);
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        gc.setServiceName("I%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setControllerName("%sController");
        mpg.setGlobalConfig(gc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };

        // 如果模板引擎是 velocity
        String templatePath = "/templates/mapper.xml.vm";

        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        focList.add(new FileOutConfig(templatePath) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return outputPath + "/src/main/resources/mapper/" + dbShortName
                        + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });

        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);

        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        //控制 不生成 controller
        templateConfig.setController("");

        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // templateConfig.setService();
        // templateConfig.setController();

        templateConfig.setXml(null);
        mpg.setTemplate(templateConfig);

        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useUnicode=true&useSSL=false&characterEncoding=utf8");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername(user);
        dsc.setPassword(password);
//        dsc.setUsername("root");
//        dsc.setPassword("1786904181Jj");
        dsc.setDbType(DbType.MYSQL);
        mpg.setDataSource(dsc);

        PackageConfig pc = new PackageConfig();
//        pc.setModuleName("mybatisplus");
        String parent = "com."+packagePath.replaceAll("\\.","/")+"."
                + outputPath.substring(outputPath.lastIndexOf("/") + 1).replaceAll("-", ".");
        pc.setParent(parent);
        pc.setEntity("entity" + "." + dbShortName);
        pc.setMapper("mapper" + "." + dbShortName);
        pc.setXml("mapper.xml");
        pc.setService("service" + "." + dbShortName);
        pc.setServiceImpl("service.impl" + "." + dbShortName);
        pc.setController("controller");
        mpg.setPackageInfo(pc);

        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEnableSqlFilter(false);
        strategy.setInclude(tables);
        strategy.setChainModel(true);
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);
        strategy.setEntityTableFieldAnnotationEnable(true);
        strategy.setVersionFieldName("version");
        strategy.setLogicDeleteFieldName("deleted");
        strategy.setTableFillList(Arrays.asList(new TableFill("create_time", FieldFill.INSERT), new TableFill("update_time", FieldFill.INSERT_UPDATE)));
        mpg.setStrategy(strategy);

        mpg.execute();
    }

    /**
     * 生成crud方法
     *
     * @param outputPath  输出目录
     * @param author      注释作者
     * @param packagePath 包路径
     * @param host        db的ip
     * @param port        db的端口
     * @param dbName      db名字
     * @param user        数据库用户名
     * @param password    数据库密码
     * @param dbShortName 包名(多数据源时需要将不同db的表放在不同包下)
     * @param mode        生成模式 all:生成facade和service facade:只生成facade service:只生成service
     * @param tables      需要生成的表数组
     */
    public static void generateCrud(String outputPath, String author,String packagePath, String host, String port,
                                    String dbName, String user, String password, String dbShortName,
                                    String mode, String... tables) {
        if(StrUtil.isEmpty(packagePath)){
            packagePath = "infrastructure";
        }
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://" + host + ":" + port + "/" + dbName + "?useUnicode=true&useSSL=false&characterEncoding=utf8");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername(user);
        dsc.setPassword(password);
        dsc.setDbType(DbType.MYSQL);
        ConfigBuilder config = new ConfigBuilder(null, dsc, null, null, null);
        List<TableInfo> tableInfoList = config.getTableInfoList();

        String projectName = outputPath.substring(outputPath.lastIndexOf("/") + 1);//

        Map<String, Map<String, String>> enumMap = new HashMap<>();
        for (TableInfo tableInfo : tableInfoList) {
            String tbName = tableInfo.getName();
            if (!Arrays.asList(tables).contains(tbName)) {
                continue;
            }
            String tbCamel = StrUtil.upperFirst(StrUtil.toCamelCase(tbName));//MngMchData
            String tbShort = StrUtil.toCamelCase(tbName.substring(tbName.indexOf("_") + 1)).toLowerCase();//mchdata

            if (mode == null || mode.equals("all") || mode.equals("facade")) {
                //facade接口
                String facadeService = facadeService(projectName, author,packagePath, dbShortName, tbName, tableInfo.getComment());
                cn.hutool.core.io.file.FileWriter writer1 = new cn.hutool.core.io.file.FileWriter(outputPath +
                        "/" + projectName + "-facade/src/main/java/com/"+packagePath.replaceAll("\\.","/")+
                        "/" + projectName.replaceAll("-", "/") +
                        "/facade/service/" + dbShortName + "/IRpc" + tbCamel + "Service.java");
                writer1.write(facadeService);

                //查询dto
                String quertDTO = quertDTO(projectName, author,packagePath, dbShortName, tbName, tableInfo.getComment());
                cn.hutool.core.io.file.FileWriter writer2 = new cn.hutool.core.io.file.FileWriter(outputPath +
                        "/" + projectName + "-facade/src/main/java/com/"+packagePath.replaceAll("\\.","/")+
                        "/" + projectName.replaceAll("-", "/") +
                        "/facade/domain/request/" + dbShortName + "/" + tbShort + "/" + tbCamel + "QueryRpcDTO.java");
                writer2.write(quertDTO);

                //查询vo
                String quertVO = quertVO(enumMap, outputPath, projectName, author,packagePath, dbShortName, tbName, tableInfo.getComment(), tableInfo.getFields());
                cn.hutool.core.io.file.FileWriter writer3 = new cn.hutool.core.io.file.FileWriter(outputPath +
                        "/" + projectName + "-facade/src/main/java/com/"+packagePath.replaceAll("\\.","/")+
                        "/" + projectName.replaceAll("-", "/") +
                        "/facade/domain/response/" + dbShortName + "/" + tbShort + "/" + tbCamel + "QueryRpcVO.java");
                writer3.write(quertVO);

                //插入dto
                String insertDTO = insertDTO(projectName, author,packagePath, dbShortName, tbName, tableInfo.getComment(), tableInfo.getFields());
                cn.hutool.core.io.file.FileWriter writer4 = new cn.hutool.core.io.file.FileWriter(outputPath +
                        "/" + projectName + "-facade/src/main/java/com/"+packagePath.replaceAll("\\.","/")+
                        "/" + projectName.replaceAll("-", "/") +
                        "/facade/domain/request/" + dbShortName + "/" + tbShort + "/" + tbCamel + "InsertRpcDTO.java");
                writer4.write(insertDTO);

                //更新dto
                String updateDTO = updateDTO(projectName, author,packagePath, dbShortName, tbName, tableInfo.getComment());
                cn.hutool.core.io.file.FileWriter writer5 = new cn.hutool.core.io.file.FileWriter(outputPath +
                        "/" + projectName + "-facade/src/main/java/com/"+packagePath.replaceAll("\\.","/")+
                        "/" + projectName.replaceAll("-", "/") +
                        "/facade/domain/request/" + dbShortName + "/" + tbShort + "/" + tbCamel + "UpdateRpcDTO.java");
                writer5.write(updateDTO);

                //更新实体dto
                String updateEntityDTO = updateEntityDTO(projectName, author,packagePath, dbShortName, tbName, tableInfo.getComment(), tableInfo.getFields());
                cn.hutool.core.io.file.FileWriter writer6 = new cn.hutool.core.io.file.FileWriter(outputPath +
                        "/" + projectName + "-facade/src/main/java/com/"+packagePath.replaceAll("\\.","/")+
                        "/" + projectName.replaceAll("-", "/") +
                        "/facade/domain/request/" + dbShortName + "/" + tbShort + "/" + tbCamel + "UpdateEntityRpcDTO.java");
                writer6.write(updateEntityDTO);

                //更新条件dto
                String updateConditionDTO = updateConditionDTO(projectName, author,packagePath, dbShortName, tbName, tableInfo.getComment());
                cn.hutool.core.io.file.FileWriter writer7 = new cn.hutool.core.io.file.FileWriter(outputPath +
                        "/" + projectName + "-facade/src/main/java/com/"+packagePath.replaceAll("\\.","/")+
                        "/" + projectName.replaceAll("-", "/") +
                        "/facade/domain/request/" + dbShortName + "/" + tbShort + "/" + tbCamel + "UpdateConditionRpcDTO.java");
                writer7.write(updateConditionDTO);

                //删除dto
                String deleteDTO = deleteDTO(projectName, author,packagePath, dbShortName, tbName, tableInfo.getComment());
                cn.hutool.core.io.file.FileWriter writer8 = new cn.hutool.core.io.file.FileWriter(outputPath +
                        "/" + projectName + "-facade/src/main/java/com/"+packagePath.replaceAll("\\.","/")+
                        "/" + projectName.replaceAll("-", "/") +
                        "/facade/domain/request/" + dbShortName + "/" + tbShort + "/" + tbCamel + "DeleteRpcDTO.java");
                writer8.write(deleteDTO);
            }

            if (mode == null || mode.equals("all") || mode.equals("service")) {
                //controller
                String controller = controller(projectName, author,packagePath, dbShortName, tbName, tableInfo.getComment());
                cn.hutool.core.io.file.FileWriter writer9 = new cn.hutool.core.io.file.FileWriter(outputPath +
                        "/" + projectName + "-service/src/main/java/com/"+packagePath.replaceAll("\\.","/")+
                        "/" + projectName.replaceAll("-", "/") +
                        "/service/controller/" + dbShortName + "/" + tbCamel + "Controller.java");
                writer9.write(controller);
            }
            System.out.println("表" + tbName + "生成完成");
        }

        genEnum(author,packagePath, outputPath, projectName, dbShortName, enumMap);
    }
    private static void genEnum(String author,String packagePath, String outputPath, String projectName, String dbShortName, Map<String, Map<String, String>> enumMap) {
        String nowDateStr = DateUtil.formatDateTime(new Date());//2021-09-01 00:00:00
        List<String> transMsg = new ArrayList<>();
        for (Map.Entry<String, Map<String, String>> entry : enumMap.entrySet()) {
            for (Map.Entry<String, String> stringEntry : entry.getValue().entrySet()) {
                transMsg.add(stringEntry.getKey());
            }
        }

//        Map<String, String> translate = TranslateUtils.translate(transMsg);

        for (Map.Entry<String, Map<String, String>> entry : enumMap.entrySet()) {
            String enumClassName = entry.getKey();
            String enumName = enumClassName.substring(enumClassName.lastIndexOf(".") + 1);
            String tbShort = enumClassName.substring(0, enumClassName.lastIndexOf("."));
            tbShort = tbShort.substring(tbShort.lastIndexOf(".") + 1);
            Map<String, String> enumFieldMap = entry.getValue();
            String enumStr = "package " + enumClassName.substring(0, enumClassName.lastIndexOf(".")) + ";\n\n";
            enumStr = enumStr + "import lombok.AllArgsConstructor;\nimport lombok.Getter;\n\n";
            String classComment = "/**\n" +
                    " * " + enumName + "枚举\n" +
                    " * @author " + author + "\n" +
                    " * @data " + nowDateStr + "\n" +
                    " */\n";
            enumStr = enumStr + classComment;
            enumStr = enumStr + "@Getter\n@AllArgsConstructor\npublic enum " + enumName + " {\n\n";

            String codeType = "String";
            for (Map.Entry<String, String> stringEntry : enumFieldMap.entrySet()) {
                String msg = stringEntry.getKey();
                String code = stringEntry.getValue();
                if (isNumeric(code)){
                    codeType = "int";
                }

//                String fieldName = translate.get(msg).replaceAll(" ", "_").toUpperCase();
                String fieldName = "";
                fieldName = fieldName.replaceAll("-", "_");

                if (codeType.equals("String")){
                    enumStr = enumStr + "\t" + fieldName + "(\"" + code + "\", \"" + msg + "\"),\n";
                }else {
                    enumStr = enumStr + "\t" + fieldName + "(" + code + ", \"" + msg + "\"),\n";
                }
            }
            enumStr = enumStr.substring(0, enumStr.length() - 2) + ";\n\n";

            enumStr = enumStr + "\tprivate " + codeType + " code;\n\tprivate String msg;\n\n";
            enumStr = enumStr + "\tpublic static " + enumName + " getByCode(String code){\n";
            enumStr = enumStr + "\t\tfor(" + enumName + " e : " + enumName + ".values()){\n";
            enumStr = enumStr + "\t\t\tif(code.equals(e.getCode())){\n";
            enumStr = enumStr + "\t\t\t\treturn e;\n";
            enumStr = enumStr + "\t\t\t}\n";
            enumStr = enumStr + "\t\t}\n";
            enumStr = enumStr + "\t\treturn null;\n";
            enumStr = enumStr + "\t}\n";
            enumStr = enumStr + "}";
            cn.hutool.core.io.file.FileWriter writer = new cn.hutool.core.io.file.FileWriter(outputPath +
                    "/" + projectName + "-facade/src/main/java/com/"+packagePath.replaceAll("\\.","/")+
                    "/" + projectName.replaceAll("-", "/") +
                    "/facade/enums/" + dbShortName + "/" + tbShort + "/" + enumName + ".java");
            writer.write(enumStr);
        }
    }

    private static String controller(String projectName, String author, String packagePath,String dbShortName, String tbName, String tbComment) {
        String TbCamel = StrUtil.upperFirst(StrUtil.toCamelCase(tbName));//
        String tbCamel = StrUtil.lowerFirst(TbCamel);//
        String tbShort = StrUtil.toCamelCase(tbName.substring(tbName.indexOf("_") + 1)).toLowerCase();//
        String shortPackage = projectName.replaceAll("-", ".");//

        InputStream inputStream = InfrastructureOrmGenerator.class.getClassLoader().getResourceAsStream("InfrastructureTemFile.tmp");
        String read = IoUtil.read(inputStream, Charset.defaultCharset());

        read = read.replaceAll("\\[tbCamel]", tbCamel);
        read = read.replaceAll("\\[TbCamel]", TbCamel);
        read = read.replaceAll("\\[tbShort]", tbShort);
        read = read.replaceAll("\\[dbShortName]", dbShortName);
        read = read.replaceAll("\\[project]", shortPackage);
        read = read.replaceAll("\\[packagePath]", packagePath);
        return read;
    }

    private static String facadeService(String projectName, String author,String packagePath, String dbShortName, String tbName, String tbComment) {
        String nowDateStr = DateUtil.formatDateTime(new Date());//2021-09-01 00:00:00
        String tbCamel = StrUtil.upperFirst(StrUtil.toCamelCase(tbName));//
        String tbShort = StrUtil.toCamelCase(tbName.substring(tbName.indexOf("_") + 1)).toLowerCase();//mchdata
        String tbShortCamel = StrUtil.toCamelCase(tbName.substring(tbName.indexOf("_") + 1));//mchData
        String shortPackage = projectName.replaceAll("-", ".");//
        String pathName = projectName.contains("-") ? projectName.substring(0, projectName.indexOf("-")) : projectName;//

        String packageStr = "package com."+packagePath+"." + shortPackage + ".facade.service." + dbShortName + ";\n\n";
        String importStr = "com.infrastructure.common.PageResponse;\n" +
                "import com.infrastructure.common.Result;\n" +
                "import com."+packagePath+"." + shortPackage + ".facade.domain.request." + dbShortName + "." + tbShort + "." + tbCamel + "DeleteRpcDTO;\n" +
                "import com."+packagePath+"." + shortPackage + ".facade.domain.request." + dbShortName + "." + tbShort + "." + tbCamel + "InsertRpcDTO;\n" +
                "import com."+packagePath+"." + shortPackage + ".facade.domain.request." + dbShortName + "." + tbShort + "." + tbCamel + "QueryRpcDTO;\n" +
                "import com."+packagePath+"." + shortPackage + ".facade.domain.request." + dbShortName + "." + tbShort + "." + tbCamel + "UpdateRpcDTO;\n" +
                "import com."+packagePath+"." + shortPackage + ".facade.domain.response." + dbShortName + "." + tbShort + "." + tbCamel + "QueryRpcVO;\n" +
                "import org.springframework.cloud.openfeign.FeignClient;\n" +
                "import org.springframework.web.bind.annotation.RequestBody;\n" +
                "import org.springframework.web.bind.annotation.PostMapping;\n" +
                "import org.springframework.web.bind.annotation.GetMapping;\n" +
                "import org.springframework.web.bind.annotation.RequestParam;\n" +
                "import java.util.List;\n\n";
        String classComment = "/**\n" +
                " * " + tbComment + "基础服务\n" +
                " * @author " + author + "\n" +
                " * @data " + nowDateStr + "\n" +
                " */\n";
        String feignStr = "@FeignClient(value = \"" + projectName + "-service\", url = \"${" + shortPackage + ".service.feign.url:}\")\n";
        String classStr = "public interface IRpc" + tbCamel + "Service {\n\n";
        String pageMethodStr = "    /**\n" +
                "     * 分页查询\n" +
                "     * @param dto\n" +
                "     * @return\n" +
                "     */\n" +
                "    @PostMapping(\"/" + pathName + "/core/" + dbShortName + "/" + tbShortCamel + "/page\")\n" +
                "    public Result<PageResponse<" + tbCamel + "QueryRpcVO>> page(@RequestBody " + tbCamel + "QueryRpcDTO dto);\n\n";
        String queryMethodStr = "    /**\n" +
                "     * 条件查询\n" +
                "     * @param dto\n" +
                "     * @return\n" +
                "     */\n" +
                "    @PostMapping(\"/" + pathName + "/core/" + dbShortName + "/" + tbShortCamel + "/query\")\n" +
                "    public Result<List<" + tbCamel + "QueryRpcVO>> query(@RequestBody " + tbCamel + "QueryRpcDTO dto);\n\n";
        String queryByIdMethodStr = "    /**\n" +
                "     * 根据id查询\n" +
                "     * @param id\n" +
                "     * @return\n" +
                "     */\n" +
                "    @GetMapping(\"/" + pathName + "/core/" + dbShortName + "/" + tbShortCamel + "/queryById\")\n" +
                "    public Result<" + tbCamel + "QueryRpcVO> queryById(@RequestParam Integer id);\n\n";
        String insertMethodStr = "    /**\n" +
                "     * 插入\n" +
                "     * @param dto\n" +
                "     * @return\n" +
                "     */\n" +
                "    @PostMapping(\"/" + pathName + "/core/" + dbShortName + "/" + tbShortCamel + "/insert\")\n" +
                "    public Result<Boolean> insert(@RequestBody " + tbCamel + "InsertRpcDTO dto);\n\n";
        String batchInsertMethodStr = "    /**\n" +
                "     * 批量插入\n" +
                "     * @param listDto\n" +
                "     * @return\n" +
                "     */\n" +
                "    @PostMapping(\"/" + pathName + "/core/" + dbShortName + "/" + tbShortCamel + "/batchInsert\")\n" +
                "    public Result<Boolean> batchInsert(@RequestBody List<" + tbCamel + "InsertRpcDTO> listDto);\n\n";
        String updateMethodStr = "    /**\n" +
                "     * 更新\n" +
                "     * @param dto\n" +
                "     * @return\n" +
                "     */\n" +
                "    @PostMapping(\"/" + pathName + "/core/" + dbShortName + "/" + tbShortCamel + "/update\")\n" +
                "    public Result<Boolean> update(@RequestBody " + tbCamel + "UpdateRpcDTO dto);\n\n";
        String deleteMethodStr = "    /**\n" +
                "     * 删除\n" +
                "     * @param dto\n" +
                "     * @return\n" +
                "     */\n" +
                "    @PostMapping(\"/" + pathName + "/core/" + dbShortName + "/" + tbShortCamel + "/delete\")\n" +
                "    public Result<Boolean> delete(@RequestBody " + tbCamel + "DeleteRpcDTO dto);\n";
        return packageStr + importStr + classComment + feignStr + classStr + pageMethodStr + queryMethodStr + queryByIdMethodStr
                + insertMethodStr + batchInsertMethodStr + updateMethodStr + deleteMethodStr + "}";
    }

    private static String quertDTO(String projectName, String author,String packagePath, String dbShortName, String tbName, String tbComment) {
        String nowDateStr = DateUtil.formatDateTime(new Date());//2021-09-01 00:00:00
        String tbCamel = StrUtil.upperFirst(StrUtil.toCamelCase(tbName));//MngMchData
        String tbShort = StrUtil.toCamelCase(tbName.substring(tbName.indexOf("_") + 1)).toLowerCase();//mchdata
        String tbShortCamel = StrUtil.toCamelCase(tbName.substring(tbName.indexOf("_") + 1));//mchData
        String shortPackage = projectName.replaceAll("-", ".");//
//        String pathName = projectName.substring(0, projectName.indexOf("-"));//

        String packageStr = "package com."+packagePath+"." + shortPackage + ".facade.domain.request." + dbShortName + "." + tbShort + ";\n\n";
        String importStr = "com.infrastructure.common.PageResponse;\n" +
                "import lombok.AllArgsConstructor;\n" +
                "import lombok.NoArgsConstructor;\n" +
                "import lombok.Builder;\n" +
                "import lombok.Data;\n" +
                "import java.io.Serializable;\n\n@Data\n@Builder\n@NoArgsConstructor\n@AllArgsConstructor\n";
        String classStr = "public class " + tbCamel + "QueryRpcDTO extends PageRequest implements Serializable {\n\n";
        String idFeild = "    /**\n" +
                "     * 主键\n" +
                "     */\n" +
                "    private Integer id;\n";
        return packageStr + importStr + classStr + idFeild + "}";
    }

    private static String quertVO(Map<String, Map<String, String>> enumMap, String outputPath, String projectName, String author,String packagePath,
                                  String dbShortName, String tbName, String tbComment, List<TableField> fields) {
        String tbCamel = StrUtil.upperFirst(StrUtil.toCamelCase(tbName));//MngMchData
        String tbShort = StrUtil.toCamelCase(tbName.substring(tbName.indexOf("_") + 1)).toLowerCase();//mchdata
        String shortPackage = projectName.replaceAll("-", ".");//

        String packageStr = "package com."+packagePath+"." + shortPackage + ".facade.domain.response." + dbShortName + "." + tbShort + ";\n\n";
        String importStr = "import lombok.Data;\n" +
                "import java.io.Serializable;\n\n@Data\n";
        String classStr = "public class " + tbCamel + "QueryRpcVO implements Serializable {\n\n";
        String fieldsStr = "";
        boolean needDate = false;
        boolean needBigDecimal = false;

        for (TableField field : fields) {
            if ("deleted".equals(field.getName())) {
                continue;
            }

            String type = field.getColumnType().getType();
            if ("LocalDateTime".equals(type)) {
                needDate = true;
                type = "Date";
            }

            if ("BigDecimal".equals(type)) {
                needBigDecimal = true;
            }

            fieldsStr = fieldsStr + "    /**\n" +
                    "     * " + field.getComment() + "\n" +
                    "     */\n" +
                    "    private " + type + " " + StrUtil.toCamelCase(field.getName()) + ";\n\n";


            if (field.getComment().contains(":")) {
                String enumClassName = "com."+packagePath+"." + shortPackage + ".facade.enums." + dbShortName + "." + tbShort + "." + tbCamel + StrUtil.upperFirst(StrUtil.toCamelCase(field.getName())) + "Enum";

                Map<String, String> enumFieldMap = new LinkedHashMap<>();
                String[] splitArr = field.getComment().split(" ");
                for (int i = 1; i < splitArr.length; i++) {
                    String code = splitArr[i].split(":")[0];
                    String msg = splitArr[i].split(":")[1];
                    enumFieldMap.put(msg, code);
                }
                enumMap.put(enumClassName, enumFieldMap);
            }
        }

        if (needDate) {
            importStr = "import java.util.Date;\n" + importStr;
        }

        if (needBigDecimal) {
            importStr = "import java.math.BigDecimal;\n" + importStr;
        }
        return packageStr + importStr + classStr + fieldsStr.substring(0, fieldsStr.length() - 1) + "}";
    }

    private static String insertDTO(String projectName, String author,String packagePath, String dbShortName, String tbName, String tbComment, List<TableField> fields) {
        String tbCamel = StrUtil.upperFirst(StrUtil.toCamelCase(tbName));//MngMchData
        String tbShort = StrUtil.toCamelCase(tbName.substring(tbName.indexOf("_") + 1)).toLowerCase();//mchdata
        String shortPackage = projectName.replaceAll("-", ".");//

        String packageStr = "package com."+packagePath+"." + shortPackage + ".facade.domain.request." + dbShortName + "." + tbShort + ";\n\n";
        String importStr = "import lombok.Data;\n" +
                "import lombok.AllArgsConstructor;\n" +
                "import lombok.NoArgsConstructor;\n" +
                "import lombok.Builder;\n" +
                "import lombok.Data;\n" +
                "import java.io.Serializable;\n\n@Data\n@Builder\n@NoArgsConstructor\n@AllArgsConstructor\n";
        String classStr = "public class " + tbCamel + "InsertRpcDTO implements Serializable {\n\n";
        String fieldsStr = "";
        boolean needDate = false;
        boolean needBigDecimal = false;
        for (TableField field : fields) {
            if ("deleted".equals(field.getName()) || "id".equals(field.getName()) ||
                    "create_time".equals(field.getName()) || "gmt_modified".equals(field.getName())
                    || "version".equals(field.getName())) {
                continue;
            }

            String type = field.getColumnType().getType();
            if ("LocalDateTime".equals(type)) {
                needDate = true;
                type = "Date";
            }

            if ("BigDecimal".equals(type)) {
                needBigDecimal = true;
            }

            fieldsStr = fieldsStr + "    /**\n" +
                    "     * " + field.getComment() + "\n" +
                    "     */\n" +
                    "    private " + type + " " + StrUtil.toCamelCase(field.getName()) + ";\n\n";
        }

        if (needDate) {
            importStr = "import java.util.Date;\n" + importStr;
        }

        if (needBigDecimal) {
            importStr = "import java.math.BigDecimal;\n" + importStr;
        }
        return packageStr + importStr + classStr + fieldsStr.substring(0, fieldsStr.length() - 1) + "}";
    }

    private static String updateDTO(String projectName, String author,String packagePath, String dbShortName, String tbName, String tbComment) {
        String tbCamel = StrUtil.upperFirst(StrUtil.toCamelCase(tbName));//MngMchData
        String tbShort = StrUtil.toCamelCase(tbName.substring(tbName.indexOf("_") + 1)).toLowerCase();//mchdata
        String shortPackage = projectName.replaceAll("-", ".");//

        String packageStr = "package com."+packagePath+"." + shortPackage + ".facade.domain.request." + dbShortName + "." + tbShort + ";\n\n";
        String importStr = "import lombok.AllArgsConstructor;\n" +
                "import lombok.NoArgsConstructor;\n" +
                "import lombok.Builder;\n" +
                "import lombok.Data;\n" +
                "import java.io.Serializable;\n\n@Data\n@Builder\n@NoArgsConstructor\n@AllArgsConstructor\n";
        String classStr = "public class " + tbCamel + "UpdateRpcDTO implements Serializable {\n\n";
        String idFeild = "    /**\n" +
                "     * 更新实体\n" +
                "     */\n" +
                "    " + tbCamel + "UpdateEntityRpcDTO entity;\n\n" +
                "    /**\n" +
                "     * 更新条件\n" +
                "     */\n" +
                "    " + tbCamel + "UpdateConditionRpcDTO condition;\n";
        return packageStr + importStr + classStr + idFeild + "}";
    }

    private static String updateEntityDTO(String projectName, String author,String packagePath, String dbShortName, String tbName, String tbComment, List<TableField> fields) {
        String tbCamel = StrUtil.upperFirst(StrUtil.toCamelCase(tbName));//
        String tbShort = StrUtil.toCamelCase(tbName.substring(tbName.indexOf("_") + 1)).toLowerCase();//
        String shortPackage = projectName.replaceAll("-", ".");//

        String packageStr = "package com."+packagePath+"." + shortPackage + ".facade.domain.request." + dbShortName + "." + tbShort + ";\n\n";
        String importStr = "import lombok.AllArgsConstructor;\n" +
                "import lombok.NoArgsConstructor;\n" +
                "import lombok.Builder;\n" +
                "import lombok.Data;\n" +
                "import java.io.Serializable;\n\n@Data\n@Builder\n@NoArgsConstructor\n@AllArgsConstructor\n";
        ;
        String classStr = "public class " + tbCamel + "UpdateEntityRpcDTO implements Serializable {\n\n";
        String fieldsStr = "";
        boolean needDate = false;
        boolean needBigDecimal = false;
        for (TableField field : fields) {
            if ("deleted".equals(field.getName()) || "id".equals(field.getName()) ||
                    "create_time".equals(field.getName()) || "update_time".equals(field.getName())) {
                continue;
            }

            String type = field.getColumnType().getType();
            if ("LocalDateTime".equals(type)) {
                needDate = true;
                type = "Date";
            }

            if ("BigDecimal".equals(type)) {
                needBigDecimal = true;
            }

            fieldsStr = fieldsStr + "    /**\n" +
                    "     * " + field.getComment() + "\n" +
                    "     */\n" +
                    "    private " + type + " " + StrUtil.toCamelCase(field.getName()) + ";\n\n";
        }

        if (needDate) {
            importStr = "import java.util.Date;\n" + importStr;
        }

        if (needBigDecimal) {
            importStr = "import java.math.BigDecimal;\n" + importStr;
        }
        return packageStr + importStr + classStr + fieldsStr.substring(0, fieldsStr.length() - 1) + "}";
    }

    private static String updateConditionDTO(String projectName, String author,String packagePath, String dbShortName, String tbName, String tbComment) {
        String tbCamel = StrUtil.upperFirst(StrUtil.toCamelCase(tbName));//
        String tbShort = StrUtil.toCamelCase(tbName.substring(tbName.indexOf("_") + 1)).toLowerCase();//
        String shortPackage = projectName.replaceAll("-", ".");//

        String packageStr = "package com."+packagePath+"." + shortPackage + ".facade.domain.request." + dbShortName + "." + tbShort + ";\n\n";
        String importStr = "import lombok.AllArgsConstructor;\n" +
                "import lombok.NoArgsConstructor;\n" +
                "import lombok.Builder;\n" +
                "import lombok.Data;\n" +
                "import java.io.Serializable;\n\n@Data\n@Builder\n@NoArgsConstructor\n@AllArgsConstructor\n";
        ;
        String classStr = "public class " + tbCamel + "UpdateConditionRpcDTO implements Serializable {\n\n";
        String idFeild = "    /**\n" +
                "     * 主键\n" +
                "     */\n" +
                "    private Integer id;\n";
        return packageStr + importStr + classStr + idFeild + "}";
    }

    private static String deleteDTO(String projectName, String author,String packagePath, String dbShortName, String tbName, String tbComment) {
        String tbCamel = StrUtil.upperFirst(StrUtil.toCamelCase(tbName));//
        String tbShort = StrUtil.toCamelCase(tbName.substring(tbName.indexOf("_") + 1)).toLowerCase();//
        String shortPackage = projectName.replaceAll("-", ".");//

        String packageStr = "package com."+packagePath+"." + shortPackage + ".facade.domain.request." + dbShortName + "." + tbShort + ";\n\n";
        String importStr = "import lombok.AllArgsConstructor;\n" +
                "import lombok.NoArgsConstructor;\n" +
                "import lombok.Builder;\n" +
                "import lombok.Data;\n" +
                "import java.io.Serializable;\n\n@Data\n@Builder\n@NoArgsConstructor\n@AllArgsConstructor\n";
        ;
        String classStr = "public class " + tbCamel + "DeleteRpcDTO implements Serializable {\n\n";
        String idFeild = "    /**\n" +
                "     * 主键\n" +
                "     */\n" +
                "    private Integer id;\n";
        return packageStr + importStr + classStr + idFeild + "}";
    }
}
