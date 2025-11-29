import request from '@/utils/request'

export const getPermissionList = (params) => {
  return request({
    url: '/permissions',
    method: 'get',
    params
  })
}

export const getPermissionById = (id) => {
  return request({
    url: `/permissions/${id}`,
    method: 'get'
  })
}

export const createPermission = (data) => {
  return request({
    url: '/permissions',
    method: 'post',
    data
  })
}

export const updatePermission = (id, data) => {
  return request({
    url: `/permissions/${id}`,
    method: 'put',
    data
  })
}

export const deletePermission = (id) => {
  return request({
    url: `/permissions/${id}`,
    method: 'delete'
  })
}

