package com.crazymaker.cloud.ha.middleware.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//@RestController
@RequestMapping("/api/articleController/")
@Api(tags = "搜索的demo")
@Slf4j
public class ArticleController {

    @Autowired
    RestHighLevelClient highLevelClient;

    //搜索服务
    @ApiOperation(value = "根据请求参数值")
    @RequestMapping(value = "/param", method = RequestMethod.GET)
    public String search(@RequestParam(value = "title", required = false) String title) {
        String index = "article";
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSource = new SearchSourceBuilder()
                .query(QueryBuilders.matchQuery("title", title))
                .sort("_score", SortOrder.DESC)
                .size(5);


        searchSource.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(searchSource);
        try {
            SearchResponse response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Arrays.stream(response.getHits().getHits())
                    .forEach(i -> {
                        System.out.println(i.getIndex());
                        System.out.println(i.getFields());
                        System.out.println(i.getType());
                        System.out.println(i.getSourceAsString());
                    });
            System.out.println(response.getHits().getTotalHits());
            return "true";
        } catch (IOException e) {
            e.printStackTrace();
            return "false";
        }
    }


    public void testAddDoc() throws IOException {

        UpdateRequest updateRequest = new UpdateRequest("index", "id");
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("keywords", "key");
        hashMap.put("question", "qa.getQuestion()");
        updateRequest.doc(JSON.toJSONString(hashMap), XContentType.JSON);
        highLevelClient.update(updateRequest, RequestOptions.DEFAULT);

    }

    public boolean testEsRestClient() {
        String index = "article";
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("city", "北京市"));
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            Arrays.stream(response.getHits().getHits())
                    .forEach(i -> {
                        System.out.println(i.getIndex());
                        System.out.println(i.getFields());
                        System.out.println(i.getType());
                        System.out.println(i.getSourceAsString());
                    });
            System.out.println(response.getHits().getTotalHits());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void create(String index) throws IOException {
        // 创建索引请求
        CreateIndexRequest request = new CreateIndexRequest(index);

        // 配置settings
        request.settings(
                Settings.builder()
                        .put("index.number_of_shards", 1)
                        .put("index.number_of_replicas", 1)
        );

        // 配置mapping
        XContentBuilder mapping = JsonXContent.contentBuilder()
                .startObject()
                .startObject("properties")
                .startObject("keywords")
                .field("type", "text")
                .startObject("fields")
                .startObject("title_ik_smart")
                .field("type", "text")
                .field("analyzer", "ik_smart")
                .endObject()
                .startObject("title_ik_max_word")
                .field("type", "text")
                .field("analyzer", "ik_max_word")
                .endObject()
                .endObject()
                .endObject()
                // field question
                .startObject("question")
                .field("type", "text")
                .startObject("fields")
                .startObject("keyword")
                .field("type", "keyword")
                .endObject()
                .endObject()
                .endObject()
                // field hits
                .startObject("hits")
                .field("type", "long")
                .endObject()
                .endObject()
                .endObject();
        request.mapping("article2", mapping);
        highLevelClient.indices().create(request, RequestOptions.DEFAULT);

    }
}
