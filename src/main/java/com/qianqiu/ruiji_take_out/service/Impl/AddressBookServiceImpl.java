package com.qianqiu.ruiji_take_out.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qianqiu.ruiji_take_out.mapper.AddressBookMapper;
import com.qianqiu.ruiji_take_out.pojo.AddressBook;
import com.qianqiu.ruiji_take_out.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
