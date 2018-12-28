package com.junevay.mapper;

import java.util.List;

import com.junevay.pojo.SearchRecords;
import com.junevay.utils.MyMapper;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
	
	public List<String> getHotwords();
}