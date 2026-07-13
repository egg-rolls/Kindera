import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(config => {
  const user = localStorage.getItem('user')
  if (user) {
    config.headers['X-User-Id'] = JSON.parse(user).id
  }
  return config
})

// 响应拦截器
api.interceptors.response.use(
  response => response.data,
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

// 认证 API
export const authApi = {
  login: (data) => api.post('/auth/login', data)
}

// 幼儿 API
export const childApi = {
  getAll: (params) => api.get('/children', { params }),
  getById: (id) => api.get(`/children/${id}`),
  create: (data) => api.post('/children', data),
  update: (id, data) => api.put(`/children/${id}`, data),
  delete: (id) => api.delete(`/children/${id}`)
}

// 班级 API
export const classApi = {
  getAll: () => api.get('/classes')
}

// 课程 API
export const courseApi = {
  getAll: () => api.get('/courses'),
  create: (data) => api.post('/courses', data),
  update: (id, data) => api.put(`/courses/${id}`, data),
  delete: (id) => api.delete(`/courses/${id}`),
  getChildCourses: (childId) => api.get(`/children/${childId}/courses`),
  selectCourse: (childId, courseId) => api.post(`/children/${childId}/courses/${courseId}`),
  dropCourse: (childId, courseId) => api.delete(`/children/${childId}/courses/${courseId}`)
}

// 食谱 API
export const menuApi = {
  getDishes: () => api.get('/dishes'),
  createDish: (data) => api.post('/dishes', data),
  getCurrentWeek: () => api.get('/menus/current'),
  arrangeMenu: (data) => api.post('/menus', data)
}

// 考勤 API
export const attendanceApi = {
  getRecords: (params) => api.get('/attendance', { params }),
  record: (data) => api.post('/attendance', data),
  batchRecord: (data) => api.post('/attendance/batch', data)
}

// 调班 API
export const transferApi = {
  getAll: () => api.get('/transfers'),
  transfer: (data) => api.post('/transfers', data)
}

// 统计 API
export const statisticsApi = {
  getClassStats: () => api.get('/statistics/classes'),
  getCourseStats: () => api.get('/statistics/courses'),
  getGradeStats: () => api.get('/statistics/grades'),
  getAttendanceRate: (params) => api.get('/statistics/attendance', { params })
}

export default api
