{{ config(
    sort = "_airbyte_emitted_at",
    unique_key = env_var('AIRBYTE_DEFAULT_UNIQUE_KEY', '_airbyte_ab_id'),
    schema = "_airbyte_test_normalization",
    tags = [ "nested-intermediate" ]
) }}
-- SQL model to build a hash column based on the values of this record
select
    {{ dbt_utils.surrogate_key([
        '_airbyte_nested_stream_with_complex_columns_resulting_into_long_names_hashid',
        array_to_string('double_array_data'),
        array_to_string('data'),
        array_to_string(adapter.quote('column`_\'with""_quotes')),
    ]) }} as _airbyte_partition_hashid,
    tmp.*
from {{ ref('nested_stream_with_complex_columns_resulting_into_long_names_partition_ab2') }} tmp
-- partition at nested_stream_with_complex_columns_resulting_into_long_names/partition
where 1 = 1

