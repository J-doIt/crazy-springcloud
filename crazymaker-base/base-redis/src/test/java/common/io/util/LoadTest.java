package common.io.util;

import com.crazymaker.springcloud.common.util.IOUtil;
import org.junit.Test;

public class LoadTest
{

    @Test
    public void testLoadLua()
    {

        String lockLua = "script/lock.lua";

        String s = IOUtil.loadJarFile(this.getClass().getClassLoader(), lockLua);
        System.out.println("s = " + s);

    }
}
