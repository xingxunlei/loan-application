--自动放款总开关Key
local totalSwitch = '{autoLoan}YM_b_autoLoan_status'

--自动放款当前金额Key
local amount = '{autoLoan}YM_b_autoLoan_amount'

--自动放款当前并发Key
local concurrency = '{autoLoan}YM_b_autoLoan_concurrency'

--自动放款金额限制Key
local amountLimit = '{autoLoan}YM_b_autoLoan_amount_limit'

--自动放款并发限制Key
local concurrencyLimit = '{autoLoan}YM_b_autoLoan_concurrency_limit'


--自动放款当前时间，不能使用time指令获取redis时间，需要外部传递，参见http://jahu.iteye.com/blog/2378364
local status = ARGV[1]

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

--重置状态
local function reset()
    redis.call('SET', amount, 0)
    redis.call('SET', concurrency, 0)
end

if isEmptyValue(status) then
    return '{result:0, desc:"自动放款状态不能为空"}'
end

--放款金额格式不正确，返回0，走人工审核
if tonumber(status) == nil then
    return '{result:0, desc:"自动放款状态不正确"}'
end

local currentStatus = redis.call('GET', totalSwitch)

if tonumber(status) ~=  tonumber(currentStatus) then
    if tonumber(status) == 1 then
        reset()
    end
    redis.call('SET', totalSwitch, status)
end

return '{result:1, status: ' .. status .. '}'