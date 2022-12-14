package kafka;

import com.crazymaker.springcloud.email.start.EmailApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * GenUtils测试用例
 *
 * @author zlt
 * @date 2019/5/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmailApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EntityTest
{


    @Test
    public void testCase()
    {

    }

}
