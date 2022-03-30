package com.querifylabs.blog.optimizer;

import com.blazingdb.calcite.drill.DrillStatsTable;
import com.querifylabs.blog.optimizer.join.HashJoinPrule;
import com.querifylabs.blog.optimizer.join.JoinPruleBase;
import org.apache.calcite.adapter.enumerable.EnumerableConvention;
import org.apache.calcite.adapter.enumerable.EnumerableRules;
import org.apache.calcite.config.CalciteSystemProperty;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.externalize.RelWriterImpl;
import org.apache.calcite.rel.rules.CoreRules;
import org.apache.calcite.sql.SqlExplainLevel;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.tools.RuleSet;
import org.apache.calcite.tools.RuleSets;
import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class OptimizerTest {

    public static final RelOptRule ENUMERABLE_JOIN_RULE =
            HashJoinPrule.DEFAULT_CONFIG.toRule(HashJoinPrule.class);

    @Test
    public void test_tpch_q6() throws Exception {
        String datasetDirPath = "/Users/aocsa/datasets/TPCH_0.05/";

        Map<String, List<Map.Entry<String, CatalogColumnDataType>>> map = new HashMap<>();
        map.put("customer",
                Arrays.asList(new AbstractMap.SimpleEntry<>("c_custkey", CatalogColumnDataType.INT32),
                        new AbstractMap.SimpleEntry<>("c_name", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("c_address", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("c_nationkey", CatalogColumnDataType.INT32),
                        new AbstractMap.SimpleEntry<>("c_phone", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("c_acctbal", CatalogColumnDataType.DOUBLE),
                        new AbstractMap.SimpleEntry<>("c_mktsegment", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("c_comment", CatalogColumnDataType.STRING)));
        map.put("region",
                Arrays.asList(new AbstractMap.SimpleEntry<>("r_regionkey", CatalogColumnDataType.INT32),
                        new AbstractMap.SimpleEntry<>("r_name", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("r_comment", CatalogColumnDataType.STRING)));
        map.put("nation",
                Arrays.asList(new AbstractMap.SimpleEntry<>("n_nationkey", CatalogColumnDataType.INT32),
                        new AbstractMap.SimpleEntry<>("n_name", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("n_regionkey", CatalogColumnDataType.INT32),
                        new AbstractMap.SimpleEntry<>("n_comment", CatalogColumnDataType.STRING)));
        map.put("orders",
                Arrays.asList(new AbstractMap.SimpleEntry<>("o_orderkey", CatalogColumnDataType.INT64),
                        new AbstractMap.SimpleEntry<>("o_custkey", CatalogColumnDataType.INT32),
                        new AbstractMap.SimpleEntry<>("o_orderstatus", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("o_totalprice", CatalogColumnDataType.DOUBLE),
                        new AbstractMap.SimpleEntry<>("o_orderdate", CatalogColumnDataType.TIMESTAMP),
                        new AbstractMap.SimpleEntry<>("o_orderpriority", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("o_clerk", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("o_shippriority", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("o_comment", CatalogColumnDataType.STRING)));
        map.put("supplier",
                Arrays.asList(new AbstractMap.SimpleEntry<>("s_suppkey", CatalogColumnDataType.INT64),
                        new AbstractMap.SimpleEntry<>("s_name", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("s_address", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("s_nationkey", CatalogColumnDataType.INT32),
                        new AbstractMap.SimpleEntry<>("s_phone", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("s_acctbal", CatalogColumnDataType.DOUBLE),
                        new AbstractMap.SimpleEntry<>("s_comment", CatalogColumnDataType.STRING)));
        map.put("lineitem",
                Arrays.asList(new AbstractMap.SimpleEntry<>("l_orderkey", CatalogColumnDataType.INT64),
                        new AbstractMap.SimpleEntry<>("l_partkey", CatalogColumnDataType.INT64),
                        new AbstractMap.SimpleEntry<>("l_suppkey", CatalogColumnDataType.INT64),
                        new AbstractMap.SimpleEntry<>("l_linenumber", CatalogColumnDataType.INT32),
                        new AbstractMap.SimpleEntry<>("l_quantity", CatalogColumnDataType.DOUBLE),
                        new AbstractMap.SimpleEntry<>("l_extendedprice", CatalogColumnDataType.DOUBLE),
                        new AbstractMap.SimpleEntry<>("l_discount", CatalogColumnDataType.DOUBLE),
                        new AbstractMap.SimpleEntry<>("l_tax", CatalogColumnDataType.DOUBLE),
                        new AbstractMap.SimpleEntry<>("l_returnflag", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("l_linestatus", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("l_shipdate", CatalogColumnDataType.TIMESTAMP),
                        new AbstractMap.SimpleEntry<>("l_commitdate", CatalogColumnDataType.TIMESTAMP),
                        new AbstractMap.SimpleEntry<>("l_receiptdate", CatalogColumnDataType.TIMESTAMP),
                        new AbstractMap.SimpleEntry<>("l_shipinstruct", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("l_shipmode", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("l_comment", CatalogColumnDataType.STRING)));
        map.put("part",
                Arrays.asList(new AbstractMap.SimpleEntry<>("p_partkey", CatalogColumnDataType.INT64),
                        new AbstractMap.SimpleEntry<>("p_name", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("p_mfgr", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("p_brand", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("p_type", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("p_size", CatalogColumnDataType.INT64),
                        new AbstractMap.SimpleEntry<>("p_container", CatalogColumnDataType.STRING),
                        new AbstractMap.SimpleEntry<>("p_retailprice", CatalogColumnDataType.FLOAT),
                        new AbstractMap.SimpleEntry<>("p_comment", CatalogColumnDataType.STRING)));
        map.put("partsupp",
                Arrays.asList(new AbstractMap.SimpleEntry<>("ps_partkey", CatalogColumnDataType.INT64),
                        new AbstractMap.SimpleEntry<>("ps_suppkey", CatalogColumnDataType.INT64),
                        new AbstractMap.SimpleEntry<>("ps_availqty", CatalogColumnDataType.INT64),
                        new AbstractMap.SimpleEntry<>("ps_supplycost", CatalogColumnDataType.FLOAT),
                        new AbstractMap.SimpleEntry<>("ps_comment", CatalogColumnDataType.STRING)));

//        SimpleTable lineitem = SimpleTable.newBuilder("lineitem")
//            .addField("l_quantity", SqlTypeName.DECIMAL)
//            .addField("l_extendedprice", SqlTypeName.DECIMAL)
//            .addField("l_discount", SqlTypeName.DECIMAL)
//            .addField("l_shipdate", SqlTypeName.DATE)
//            .withStats(lineItemStats)
//            .build();

        SimpleSchema.Builder schemaBuilder = SimpleSchema.newBuilder("tpch");

        for (Map.Entry<String, List<Map.Entry<String, CatalogColumnDataType>>> entry : map.entrySet()) {

            String tableName = entry.getKey();
            DrillTable.Builder builder = DrillTable.newBuilder(tableName);

            for (Map.Entry<String, CatalogColumnDataType> field : entry.getValue()) {
                //columns.add(new CatalogColumnImpl(field.getKey(), field.getValue(), order_value++));
                builder.addField(field.getKey(), field.getValue());
            }
            System.out.print("TableName: " + tableName + " = ");

            DrillStatsTable statsTable = new DrillStatsTable(datasetDirPath + tableName);
            statsTable.materialize();
            CatalogTableStatsImpl stats = new CatalogTableStatsImpl(statsTable);
            builder.withStats(stats);
            DrillTable tpchTable = builder.build();
            schemaBuilder.addTable(tpchTable);
        }
        SimpleSchema schema = schemaBuilder.build();

        Optimizer optimizer = Optimizer.create(schema);

        String sql =
                "SELECT *\n" +
                        "FROM lineitem\n" +
                        "    inner join orders on l_orderkey = o_orderkey\n" +
                        "    inner join customer on o_custkey = c_custkey\n" +
                        "    inner join partsupp on ps_partkey = l_partkey\n" +
                        "    inner join supplier on l_suppkey = s_suppkey\n" +
                        "    inner join part on ps_partkey = p_partkey";

        SqlNode sqlTree = optimizer.parse(sql);
        SqlNode validatedSqlTree = optimizer.validate(sqlTree);
        RelNode relTree = optimizer.convert(validatedSqlTree);

        print("AFTER CONVERSION", relTree);

        CalciteSystemProperty.COMMUTE = true;

        RuleSet rules = RuleSets.ofList(
            CoreRules.FILTER_TO_CALC,
            CoreRules.PROJECT_TO_CALC,
            CoreRules.FILTER_CALC_MERGE,
            CoreRules.PROJECT_CALC_MERGE,
            CoreRules.FILTER_INTO_JOIN,
//            CoreRules.JOIN_ASSOCIATE,
                CalciteSystemProperty.COMMUTE.value()
                        ? CoreRules.JOIN_ASSOCIATE
                        : CoreRules.PROJECT_MERGE,

                CoreRules.JOIN_PROJECT_BOTH_TRANSPOSE,
            CoreRules.JOIN_COMMUTE, // Works better
            EnumerableRules.ENUMERABLE_TABLE_SCAN_RULE,
            EnumerableRules.ENUMERABLE_PROJECT_RULE,
            EnumerableRules.ENUMERABLE_FILTER_RULE,
            EnumerableRules.ENUMERABLE_CALC_RULE,
            EnumerableRules.ENUMERABLE_AGGREGATE_RULE,
            OptimizerTest.ENUMERABLE_JOIN_RULE
//            EnumerableRules.ENUMERABLE_JOIN_RULE
        );


        RelNode optimizerRelTree = optimizer.optimize(
            relTree,
            relTree.getTraitSet().plus(EnumerableConvention.INSTANCE),
            rules
        );

        print("AFTER OPTIMIZATION", optimizerRelTree);
    }

    private void print(String header, RelNode relTree) {
        StringWriter sw = new StringWriter();

        sw.append(header).append(":").append("\n");

        RelWriterImpl relWriter = new RelWriterImpl(new PrintWriter(sw), SqlExplainLevel.ALL_ATTRIBUTES, true);

        relTree.explain(relWriter);

        System.out.println(sw.toString());
    }
}
