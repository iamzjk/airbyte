USE [test_normalization];
    execute('create view _airbyte_test_normalization."unnest_alias_ab1__dbt_tmp" as
    
-- SQL model to parse JSON blob stored in a single column and extract into separated field columns as described by the JSON Schema
select
    json_value(_airbyte_data, ''$."id"'') as id,
    json_query(_airbyte_data, ''$."children"'') as children,
    _airbyte_ab_id,
    _airbyte_emitted_at,
    SYSDATETIME() as _airbyte_normalized_at
from "test_normalization".test_normalization._airbyte_raw_unnest_alias as table_alias
-- unnest_alias
where 1 = 1

    ');

