import request from '@/utils/request'

export const getRoleList = (params) => {
  return request({
    url: '/roles',
    method: 'get',
    params
  })
}

export const getRoleById = (id) => {
  return request({
    url: `/roles/${id}`,
    method: 'get'
  })
}

export const createRole = (data) => {
  return request({
    url: '/roles',
    method: 'post',
    data
  })
}

export const updateRole = (id, data) => {
  return request({
    url: `/roles/${id}`,
    method: 'put',
    data
  })
}

export const deleteRole = (id) => {
  return request({
    url: `/roles/${id}`,
    method: 'delete'
  })
}

