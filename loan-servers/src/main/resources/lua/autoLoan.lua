--自动放款总开关Key
local totalSwitch = '{autoLoan}YM_b_autoLoan_status'

--自动放款金额开关Key
local amountSwitch = '{autoLoan}YM_b_autoLoan_amount_switch'

--自动放款并发开关Key
local concurrencySwitch = '{autoLoan}YM_b_autoLoan_concurrency_switch'

--自动放款上单状态开关Key
local lastOrderSwitch = '{autoLoan}YM_b_autoLoan_lastOrder_switch'

--自动放款时间开关Key
local timeSwitch = '{autoLoan}YM_b_autoLoan_time_switch'

--自动放款上单正常结清开关
local normalOrderSwitch = '{autoLoan}YM_b_autoLoan_normalOrder_switch'

--自动放款上单逾期开关
local overdueOrderSwitch = '{autoLoan}YM_b_autoLoan_overdueOrder_switch'

--自动放款无借款开关
local noneLoanSwitch = '{autoLoan}YM_b_autoLoan_noneLoan_switch'

--自动放款当前金额Key
local amount = '{autoLoan}YM_b_autoLoan_amount'

--自动放款当前并发Key
local concurrency = '{autoLoan}YM_b_autoLoan_concurrency'

--自动放款开始时间Key
local startTime = '{autoLoan}YM_b_autoLoan_startTime'

--自动放款结束时间Key
local endTime = '{autoLoan}YM_b_autoLoan_endTime'

--自动放款金额限制Key
local amountLimit = '{autoLoan}YM_b_autoLoan_amount_limit'

--自动放款并发限制Key
local concurrencyLimit = '{autoLoan}YM_b_autoLoan_concurrency_limit'

--自动放款上单逾期天数Key
local overdueOrderDay = '{autoLoan}YM_b_autoLoan_overdueOrder_day'


--自动放款当前时间，不能使用time指令获取redis时间，需要外部传递，参见http://jahu.iteye.com/blog/2378364
local current = ARGV[1]

--自动放款订单增加金额
local increaseAmount = ARGV[2]

--判断是否不为空
local function notEmptyValue(value)
	return value ~= nil and value ~= ''
end

--判断是否不为空
local function notEmptyKey(key)
	if key == nil then
		return false
	end
	local value = redis.call('GET', key)
	return notEmptyValue(value)
end

--判断是否为空
local function isEmptyValue(value)
	return value == nil or value == ''
end

--判断是否为空
local function isEmptyKey(key)
	if key == nil then
		return true
	end
	local value = redis.call('GET', key)
	return isEmptyValue(value)
end

--关闭自动放款总开关
local function closeTotalSwitch()
	redis.call('SET', totalSwitch, '0')
end

--检测自动放款总开关是否关闭
local function isTotalSwitchOff()
	return '1' ~= redis.call('GET', totalSwitch)
end

--检测小开关是否开启
local function isSwitchOn(switchKey)
	return '1' == redis.call('GET', switchKey)
end

--检测小开关是否开启
local function isSwitchOff(switchKey)
	return '1' ~= redis.call('GET', switchKey)
end

local function getValue(key)
	return redis.call('GET', key)
end

--如果总开关已经关闭，不执行后续检测，直接返回status状态，告知服务端总开关已关闭
if isTotalSwitchOff() then
	return '{result:0, status:0, desc:"放款总开关已关闭"}'
end

--检测小开关是否全部关闭，如全部关闭，关闭总开关
if isSwitchOff(timeSwitch) and isSwitchOff(concurrencySwitch) and isSwitchOff(amountSwitch) and isSwitchOff(lastOrderSwitch) then
	closeTotalSwitch()
	return '{result:0, status:0, trigger_ruler_key:"switch_code", trigger_ruler_value:"all close"}'
end

--如果时间开关开启
if isSwitchOn(timeSwitch) then

	--放款时间为空，返回0，走人工审核
	if isEmptyValue(current) then
		return '{result:0, desc:"放款时间为空"}'
	end

	--放款时间格式不正确，返回0，走人工审核
	if tonumber(current) == nil then
		return '{result:0, desc:"放款时间格式不正确"}'
	end

	--如果开始时间不为空，判断当前时间是否小于开始时间，如果是，直接返回0走人工审核
	if notEmptyKey(startTime) then
		if tonumber(current) < tonumber(getValue(startTime)) then
			return '{result:0, desc:"放款时间小于开始时间"}'
		end
	end

	--如果开始时间不为空，判断当前时间是否大于结束时间，如果是，关闭总开关
	if notEmptyKey(endTime) then
		if tonumber(current) >= tonumber(getValue(endTime)) then
			closeTotalSwitch()
			return '{result:0, status:0, switch_off:"time_switch", trigger_ruler_key:"end_time", trigger_ruler_value:' .. current ..'}'
		end
	end
end

--如果并发开关开启
if isSwitchOn(concurrencySwitch) then
	--如果过期时间没有设置成功，删除key重新设置
	local ttl = redis.call('ttl', concurrency)
	if ttl == -1 then
		redis.call('del', concurrency)
	end

	--并发数增加1
	local concurrencyValue = redis.call('incr', concurrency)

	--如果并发数为1，设置过期时间为1分钟
	if concurrencyValue == 1 then
		redis.call('expire', concurrency, 60)
	end

	--如果并发数大于并发限制，关闭总开关
	if concurrencyValue > tonumber(getValue(concurrencyLimit)) then
		closeTotalSwitch()
		return '{result:0, status:0, switch_off:"concurrency_switch", trigger_ruler_key:"concurrency_limit", trigger_ruler_value:' .. concurrencyValue ..'}'
	end
end

--放款金额为空，返回0，走人工审核
if isEmptyValue(increaseAmount) then
	return '{result:0, desc:"放款金额为空"}'
end

--放款金额格式不正确，返回0，走人工审核
if tonumber(increaseAmount) == nil then
	return '{result:0, desc:"放款金额格式不正确"}'
end

--增加放款金额
local amountValue = redis.call('incrby', amount, tonumber(increaseAmount))

--如果金额开关开启
if isSwitchOn(amountSwitch) then
	--判断放款金额是否大于金额限制，如果是，关闭总开关
	if amountValue > tonumber(getValue(amountLimit)) then
		closeTotalSwitch()
		amountValue = redis.call('incrby', amount, 0 - tonumber(increaseAmount))
		return '{result:0, status:0, switch_off:"amount_switch", trigger_ruler_key:"amount_limit", trigger_ruler_value:' .. amountValue ..'}'
	end
end

--返回审核通过，可以进行自动放款（Note:上单审核在Java端检测）
return '{result:1, loan_amount:' .. amountValue .. ', borrow_amount:' .. increaseAmount ..'}'