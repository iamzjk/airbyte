

  create or replace table `dataline-integration-testing`.test_normalization.`nested_stream_with_complex_columns_resulting_into_long_names_partition`
  partition by timestamp_trunc(_airbyte_emitted_at, day)
  cluster by _airbyte_emitted_at
  OPTIONS()
  as (
    
-- Final base SQL model
select
    _airbyte_nested_stream_with_complex_columns_resulting_into_long_names_hashid,
    double_array_data,
    DATA,
    column___with__quotes,
    _airbyte_ab_id,
    _airbyte_emitted_at,
    CURRENT_TIMESTAMP() as _airbyte_normalized_at,
    _airbyte_partition_hashid
from `dataline-integration-testing`._airbyte_test_normalization.`nested_stream_with_complex_columns_resulting_into_long_names_partition_ab3`
-- partition at nested_stream_with_complex_columns_resulting_into_long_names/partition from `dataline-integration-testing`.test_normalization.`nested_stream_with_complex_columns_resulting_into_long_names`
where 1 = 1
  );
    