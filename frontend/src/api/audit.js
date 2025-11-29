import request from '@/utils/request'

export const getAuditLogList = (params) => {
  return request({
    url: '/audit-logs',
    method: 'get',
    params
  })
}

