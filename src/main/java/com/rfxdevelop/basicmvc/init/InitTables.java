package com.rfxdevelop.basicmvc.init;

import com.rfxdevelop.basicmvc.dao.simple.SysMapper;
import com.rfxdevelop.basicmvc.model.BaseData;
import com.rfxdevelop.basicmvc.model.DbColumn;
import com.rfxdevelop.basicmvc.model.DbTable;
import lombok.extern.java.Log;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Component
@Log
public class InitTables implements CommandLineRunner{

    @Autowired(required = false)
    private SysMapper sysMapper;
    @Autowired
    private BaseData baseData;
    @Value("${tableSchema}")
    private String tableSchema;

    @Override
    public void run(String... args) throws Exception {
        Map<String,DbTable> tableMap = new HashMap<>();
        List<String> tableSchemas = new ArrayList<>();
        tableSchemas.add(tableSchema);
        List<Map<String, Object>> tableList = sysMapper.selectTables(tableSchemas);
        tableList.stream().forEach(map->{
            String table_name = MapUtils.getString(map, "table_name");
            String column_name = MapUtils.getString(map, "column_name");
            DbColumn dbColumn = DbColumn.builder().build();
            try {
                BeanUtils.populate(dbColumn,map);
            } catch (IllegalAccessException e) {
                log.info("初始化Table报错："+e.getMessage());
            } catch (InvocationTargetException e) {
                log.info("初始化Table报错："+e.getMessage());
            }
            Map<String, DbColumn> columns;
            if(tableMap.containsKey(table_name)){
                DbTable dbTable = tableMap.get(table_name);
                columns = dbTable.getColumns();
            }else{
                columns = new LinkedHashMap<>();
                DbTable dbTable = DbTable.builder()
                        .table_name(table_name)
                        .columns(columns).build();

                tableMap.put(table_name,dbTable);
            }
            columns.put(column_name,dbColumn);
        });
        baseData.setTableMap(tableMap);
    }
}
