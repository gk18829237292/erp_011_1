package com.gk.erp.constants;

/**
 * Created by pc_home on 2016/11/30.
 */

public class Constants {

    public static final String WEB_SITE ="http://192.168.1.101:8080/ERP_011/";
//    public static final String WEB_SITE ="http://10.20.4.164:8080/ERP_001/";
//    public static final String WEB_SITE ="http://172.17.12.1:8080/ERP_001/";
    public static final String API_DOMAIN = WEB_SITE+"api/";
    public static final String IMG_DOMAIN = WEB_SITE+"img/";
    //验证
    public static final String METHOD_ACCOUNT_AUTHORIZE = API_DOMAIN  + "LoginServlet";

    //验证1
    public static final String METHOD_TASK_INSERT = API_DOMAIN + "creatTask";
    public static final String METHOD_DEPARTMENT_INSERT = API_DOMAIN + "CreateDepart";

    public static final String GET_PROJECT_Depart_ALL = API_DOMAIN + "GetAll";

    public static final String GET_TASK = API_DOMAIN +"getTask";
    public static final String GET_DEPART =API_DOMAIN +"getDepart";

    public static final String GET_REPORT_ALL = API_DOMAIN +"GetAllReport";

    public static final String CREATE_REPORT = API_DOMAIN +"createReport";
    public static final String CREATE_SUPERVISE = API_DOMAIN +"createSuper";

    public static final String UPDATE_TASK = API_DOMAIN+"updateTask";


    public static final String DELETE_TASK = API_DOMAIN + "deleteTask";
    public static final String DELETE_DEPART = API_DOMAIN + "deleteDepart";
}
