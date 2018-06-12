package com.loan_server.contract_mapper;

import com.loan_entity.contract.Contract;
import tk.mybatis.mapper.common.Mapper;

public interface ContractMapper extends Mapper<Contract> {
    int insertContract(Contract contract);
}