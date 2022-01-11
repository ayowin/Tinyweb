package com.wz.tinyweb.mapper;

import com.wz.tinyweb.core.Inject;

@Inject
public class IndexMapper {

    private static class Index{
        private int id;
        private String content;

        public Index(int id,String content){
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return "Index{" +
                    "id=" + id +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    public String select(){
        Index index = new Index(1,"select api test");
        return index.toString();
    }

}
