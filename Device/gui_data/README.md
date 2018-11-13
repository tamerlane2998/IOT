Serial pin

Truyền dữ liệu giữa 2 chân pin:


Sử dụng 2 chân:
	input: 
		2 chân: digital 
			+ 1 chân truyền data
			+ 1 chân truyền tần số 
	output:
		1 chân: digital  → truyền data.
		1 chân: Interrupt  → truyền tần số.
bắt 2 trạng thái của tần số:
	- chuyển từ LOW → HIGH: nếu chân data  == HIGH là bit bắt đầu.
            - chuyền từ HIGH → LOW:  đổi bit data cần chuyền.

#include “serial_v1.h”
class Serial_make

1. open_send(pin_data,  pin_frequency, frequency)  mở cổng đẻ gửi dữ liệu
	pin_data: chân digital truyền dữ liêụ
	pin_frecquency: chân tần số truyền tần số
	frecquency : tần số chuyền.
return NULL

2.open_receive(pin_data, pin_frequency) mở cổng nhận dữ liệu
	pin_data: chân digital nhận dữ liệu
	pin_frecquency: chân  Interrupt nhận tần số
return NULL
3.write(string ) gửi 1 xâu string
	string: xâu cần gửi 
return NULL

4.available() số lương byte trong bộ nhớ đệm
return int

5.read() chả về ký tự ASCII gần nhất trong bộ nhớ đệm
return char

6.change() setup giao thức ISR cho 	attachInterrupt()


	
