package arthas;

import java.util.HashMap;

/**
 * 使用 trace 命令可以跟踪统计方法耗时。
 * 使用 monitor 命令监控统计方法的执行情况。每5秒统计一次 CheckTimeout 类的 get 方法执行情况（monitor -c 5 arthas.CheckTimeout get）。
 *
 */
public class CheckTimeout {

    public static void main(String[] args) throws Exception{

        // 运行该方法之后，使用 trace classFullName methodName（trace arthas.CheckTimeout getUser）命令开始检测耗时情况。
        // 然后在定位到某个方法比较耗时时，再使用此命令进行追踪（trace arthas.CheckTimeout get）。
        getUser(1);
    }

    public static HashMap<String, Object> getUser(Integer uid) throws Exception {
        // 模拟用户查询
        get(uid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("name", "name" + uid);
        return hashMap;
    }

    public static void get(Integer uid) throws Exception {
        check(uid);
        service(uid);
        redis(uid);
        mysql(uid);
    }

    public static void service(Integer uid) throws Exception {
        int count = 0;
        for (int i = 0; i < 10; i++) {
            count += i;
        }
        System.out.println("service  end {}" + count);
    }

    public static void redis(Integer uid) throws Exception {
        int count = 0;
        for (int i = 0; i < 10000; i++) {
            count += i;
        }
        System.out.println("redis  end {}" + count);
    }

    public static void mysql(Integer uid) throws Exception {
        long count = 0;
        for (int i = 0; i < 10000000; i++) {
            count += i;
        }
        System.out.println("mysql end {}" + count);
    }

    public static boolean check(Integer uid) throws Exception {
        if (uid == null || uid < 0) {
            System.out.println("uid不正确，uid:{}" + uid);
            throw new Exception("uid不正确");
        }
        return true;
    }


}
