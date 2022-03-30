package com.querifylabs.blog.optimizer;


import com.blazingdb.calcite.drill.NumericEquiDepthHistogram;

 import java.util.Arrays;
import java.util.List;


public class CatalogHistogramImpl   {
    public CatalogHistogramImpl(){}

    public CatalogHistogramImpl(NumericEquiDepthHistogram histogram) {
        this.numRowsPerBucket = histogram.getNumRowsPerBucket();
        this.buckets = Arrays.asList(histogram.getBuckets());
    }
    private Long id;
    private Double numRowsPerBucket;
    private List<Double> buckets;  private CatalogTableStatsImpl stats;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getNumRowsPerBucket() {
        return numRowsPerBucket;
    }

    public void setNumRowsPerBucket(Double numRowsPerBucket) {
        this.numRowsPerBucket = numRowsPerBucket;
    }

    public List<Double> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<Double> buckets) {
        this.buckets = buckets;
    }
}
