
  create view "postgres"._airbyte_test_normalization."nested_stream_with_c___names_partition_data_ab1__dbt_tmp" as (
    
-- SQL model to parse JSON blob stored in a single column and extract into separated field columns as described by the JSON Schema

select
    _airbyte_partition_hashid,
    jsonb_extract_path_text(_airbyte_nested_data, 'currency') as currency,
    _airbyte_ab_id,
    _airbyte_emitted_at,
    now() as _airbyte_normalized_at
from "postgres".test_normalization."nested_stream_with_c___long_names_partition" as table_alias
-- DATA at nested_stream_with_complex_columns_resulting_into_long_names/partition/DATA
cross join jsonb_array_elements(
        case jsonb_typeof("DATA")
        when 'array' then "DATA"
        else '[]' end
    ) as _airbyte_nested_data
where 1 = 1
and "DATA" is not null
  );
