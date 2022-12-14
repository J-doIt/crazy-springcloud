package com.crazymaker.cloud.nacos.demo.sentinel.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowItem;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Configuration
public class SentinelConfig {
    //    异常比例 (DEGRADE_GRADE_EXCEPTION_RATIO)
    private static void initDegradeRule_RATIO() {
        List<DegradeRule> rules = new ArrayList<DegradeRule>();
        DegradeRule rule = new DegradeRule();
        rule.setResource("");
        // set limit exception ratio to 0.1
        rule.setCount(0.1); // 异常率是 10%
        rule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_RATIO);// 根据每秒的异常率来限流
        rule.setTimeWindow(10);// 单位秒
        rule.setMinRequestAmount(20); // 最少要20个请求，否则不能触发限流策略。
        rules.add(rule);
        DegradeRuleManager.loadRules(rules);
    }

    private static void initDegradeRule_RT() {
        List<DegradeRule> rules = new ArrayList<DegradeRule>();
        DegradeRule rule = new DegradeRule();
        rule.setResource("");
        // set threshold rt, 10 ms
        rule.setCount(10);// 单位毫秒
        rule.setGrade(RuleConstant.DEGRADE_GRADE_RT);  // 基于响应时间的限流策略
        rule.setTimeWindow(10);
        rules.add(rule);
        DegradeRuleManager.loadRules(rules);
    }

    @PostConstruct
    public void initSentinelRule() {
        //熔断规则： 5s内调用接口出现异常次数超过5的时候, 进行熔断
        List<DegradeRule> degradeRules = new ArrayList<>();
        DegradeRule rule = new DegradeRule();
        rule.setResource("querySkusInfo");
        rule.setCount(5);
        rule.setGrade(RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT);//熔断规则
        rule.setTimeWindow(5);

        degradeRules.add(rule);
        DegradeRuleManager.loadRules(degradeRules);

//限流规则 QPS mode,
        List<FlowRule> rules = new ArrayList<FlowRule>();
        FlowRule simpleRule = new FlowRule();
        simpleRule.setResource("getOrder");
        // QPS控制在2以内
        simpleRule.setCount(2);
        // QPS限流
        simpleRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        simpleRule.setLimitApp("default");
        rules.add(simpleRule);
//        FlowRuleManager.loadRules(rules);


        // 关联模式流控  QPS控制在1以内
        String refResource = "test1_ref";
        FlowRule rRule = new FlowRule("test1")
                .setCount(1)  // QPS控制在1以内
                .setStrategy(RuleConstant.STRATEGY_RELATE)
                .setRefResource(refResource);
        rules.add(rRule);

        FlowRule warmUPRule = new FlowRule();
        warmUPRule.setResource("testWarmUP");
        warmUPRule.setCount(20);
        warmUPRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        warmUPRule.setLimitApp("default");
        warmUPRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_WARM_UP);
        warmUPRule.setWarmUpPeriodSec(10);

        rules.add(warmUPRule);

        FlowRule lineUpRule = new FlowRule();
        lineUpRule.setResource("testLineUp");
        lineUpRule.setCount(1);
        lineUpRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        lineUpRule.setLimitApp("default");
        lineUpRule.setMaxQueueingTimeMs(20 * 1000);
        // CONTROL_BEHAVIOR_DEFAULT means requests more than threshold will be rejected immediately.
        // CONTROL_BEHAVIOR_DEFAULT将超过阈值的流量立即拒绝掉.
        lineUpRule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER);
        rules.add(lineUpRule);

        FlowRuleManager.loadRules(rules);

        //热点规则 QPS mode,
        // threshold is 1 for every frequent "hot spot" parameter in index 0 (the first arg).


        ParamFlowRule pRule = new ParamFlowRule("byHotKey")
                .setParamIdx(1)
                .setCount(1);
// 针对 参数值1000，单独设置限流 QPS 阈值为 5，而不是全局的阈值 1.
        ParamFlowItem item = new ParamFlowItem().setObject(String.valueOf(1000))
                .setClassType(int.class.getName())
                .setCount(2);

        ParamFlowItem item2 = new ParamFlowItem().setObject(String.valueOf(10000))
                .setClassType(int.class.getName())
                .setCount(2);
        pRule.setParamFlowItemList(Collections.singletonList(item));

        ParamFlowRuleManager.loadRules(Collections.singletonList(pRule));


        List<SystemRule> srules = new ArrayList<>();
        SystemRule srule = new SystemRule();
        srule.setAvgRt(3000);
        srules.add(srule);
        SystemRuleManager.loadRules(srules);
    }
}