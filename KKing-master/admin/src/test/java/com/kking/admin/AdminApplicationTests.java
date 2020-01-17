package com.kking.admin;

import com.kking.generator.service.GenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminApplicationTests {

    @Autowired
    GenService genService;

    @Test
    public void contextLoads() {
        String table="t_user_info";
        String packageName="com.kking.generator";
        genService.generateCode(table,packageName);
    }

}

