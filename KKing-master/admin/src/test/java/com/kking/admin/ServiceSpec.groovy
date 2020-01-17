package com.kking.admin

import com.kking.generator.service.GenService
import com.kking.generator.service.impl.GenServiceImpl
import spock.lang.Specification

/**
 * @ClassName ServiceSpec.java* @author zhaoliancan* @version 1.0.0* @Description TODO* @createTime 2019年10月18日 15:31:00
 */
class ServiceSpec extends Specification {

    GenService genService=new GenServiceImpl()
    def "maximum of two numbers"(int a, int b, int c) {
        expect:
        Math.max(a, b) == c

        where:
        a | b | c
        1 | 3 | 3
        7 | 4 | 7
        0 | 0 | 0
    }

    def "HashMap accepts null key"() {
        given:
        def map = new HashMap()

        when:
        map.put(null, "elem")

        then:
        notThrown(NullPointerException)
    }

    def "get TableInfo"(){

    }
}
