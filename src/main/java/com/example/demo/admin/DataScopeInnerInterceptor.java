//package com.example.demo.admin;
//
//import cn.hutool.core.collection.CollUtil;
//import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
//import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
//import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
//import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
//import ggd.wuyang.basic.context.ContextUtil;
//import ggd.wuyang.basic.utils.ArgumentAssert;
//import lombok.extern.slf4j.Slf4j;
//import net.sf.jsqlparser.JSQLParserException;
//import net.sf.jsqlparser.expression.Expression;
//import net.sf.jsqlparser.expression.LongValue;
//import net.sf.jsqlparser.expression.Parenthesis;
//import net.sf.jsqlparser.expression.ValueListExpression;
//import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
//import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
//import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
//import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
//import net.sf.jsqlparser.expression.operators.relational.InExpression;
//import net.sf.jsqlparser.parser.CCJSqlParserUtil;
//import net.sf.jsqlparser.schema.Column;
//import net.sf.jsqlparser.statement.Statement;
//import net.sf.jsqlparser.statement.select.*;
//import org.apache.ibatis.executor.Executor;
//import org.apache.ibatis.mapping.BoundSql;
//import org.apache.ibatis.mapping.MappedStatement;
//import org.apache.ibatis.session.ResultHandler;
//import org.apache.ibatis.session.RowBounds;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//
//import java.util.Collections;
//import java.util.List;
//
///**
// * mybatis 数据权限拦截器
// * <p>
// *
// * @author zuihou
// * @date 2020/9/27 10:00 上午
// */
//@Slf4j
//public class DataScopeInnerInterceptor implements InnerInterceptor {
//
////    @Autowired
////    @Lazy
////    private DataScopeService dataScopeService;
////    @Autowired
////    @Lazy
////    private DataScopeContext dataScopeContext;
//
//    /**
//     * 1, 请求头携带当前页面地址
//     * 2，根据页面地址，查询该页面的列表权限（个人，当前部门，当前公司，自定义）
//     * 3. 根据类型，查询实际过滤值
//     * 4. 只拼接 where 条件
//     */
//    @Override
//    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
//        return;
//        // 解析Mapper方法上的 @DataScope 注解，并将 @DataScope 封装到 List<DataFieldProperty>
////        List<DataFieldProperty> dataFieldProperties = DataScopeHelper.getLocalDataScope();
////        DataScopeHelper.clearDataScope();
////        if (CollUtil.isEmpty(dataFieldProperties)) {
////            dataFieldProperties = ScopeUtils.buildDataScopeProperty(ms.getId());
////        }
////        if (CollUtil.isEmpty(dataFieldProperties)) {
////            return;
////        }
////        // 原始SQL
////        String originalSql = boundSql.getSql();
////        try {
////            dataFieldProperties = findDataFieldProperty(dataFieldProperties);
////            if (CollUtil.isEmpty(dataFieldProperties)) {
////                return;
////            }
////
////            String newSql;
////
////            Statement statement = CCJSqlParserUtil.parse(originalSql);
////
////            // 将数据权限控制SQL动态拼接到原始SQL中
////            this.processSelect((Select) statement, dataFieldProperties);
////
////            newSql = statement.toString();
////
////            // 拼接后的SQL替换原始SQL
////            PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
////            mpBoundSql.sql(newSql);
////        } catch (JSQLParserException e) {
////            throw ExceptionUtils.mpe("数据权限sql拼接失败, Error SQL: %s", e.getCause(), originalSql);
////        } finally {
////            DataScopeHelper.clearDataScope();
////        }
//    }
//
//
//}
