package com.shijivk.todokireader.config;

import com.shijivk.todokireader.pojo.Source;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "source")
public class SourceProps {
    private List<Source> sourceList;

    public List<Source> getSourceList() {
        return sourceList;
    }

    public void setSourceList(List<Source> sourceList) {
        this.sourceList = sourceList;
    }

    public Class<?> getSourceClass(String sourceName){
        for (Source source : this.sourceList) {
            if(source.getName().equals(sourceName)){
                try {
                    return Class.forName(source.getClassPath());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
