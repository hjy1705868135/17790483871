/**
 * 用户模型
 * 模拟数据库用户表
 */
const bcrypt = require('bcryptjs');
const { v4: uuidv4 } = require('uuid');
const logger = require('../utils/logger');

// 模拟用户数据库
const users = new Map();

// 初始化测试用户
const initTestUsers = async () => {
  const testUsers = [
    {
      id: uuidv4(),
      username: 'admin',
      password: await bcrypt.hash('admin123', 10),
      email: 'admin@example.com',
      role: 'admin',
      name: '管理员',
      createdAt: new Date(),
    },
    {
      id: uuidv4(),
      username: 'user1',
      password: await bcrypt.hash('user123', 10),
      email: 'user1@example.com',
      role: 'user',
      name: '测试用户1',
      createdAt: new Date(),
    },
    {
      id: uuidv4(),
      username: 'user2',
      password: await bcrypt.hash('user123', 10),
      email: 'user2@example.com',
      role: 'user',
      name: '测试用户2',
      createdAt: new Date(),
    },
  ];

  testUsers.forEach((user) => {
    users.set(user.id, user);
  });

  logger.info('测试用户初始化完成', { count: testUsers.length });
};

/**
 * 用户类
 */
class User {
  /**
   * 根据用户名查找用户
   * @param {string} username - 用户名
   * @returns {Object|null} 用户对象
   */
  static async findByUsername(username) {
    for (const user of users.values()) {
      if (user.username === username) {
        const { password, ...userWithoutPassword } = user;
        return userWithoutPassword;
      }
    }
    return null;
  }

  /**
   * 根据ID查找用户
   * @param {string} id - 用户ID
   * @returns {Object|null} 用户对象
   */
  static async findById(id) {
    const user = users.get(id);
    if (user) {
      const { password, ...userWithoutPassword } = user;
      return userWithoutPassword;
    }
    return null;
  }

  /**
   * 验证用户密码
   * @param {string} username - 用户名
   * @param {string} password - 密码
   * @returns {Object|null} 验证成功返回用户对象，失败返回null
   */
  static async verifyPassword(username, password) {
    for (const user of users.values()) {
      if (user.username === username) {
        const isValid = await bcrypt.compare(password, user.password);
        if (isValid) {
          const { password: pwd, ...userWithoutPassword } = user;
          return userWithoutPassword;
        }
      }
    }
    return null;
  }

  /**
   * 创建新用户
   * @param {Object} userData - 用户数据
   * @returns {Object} 创建的用户对象
   */
  static async create(userData) {
    const hashedPassword = await bcrypt.hash(userData.password, 10);
    const newUser = {
      id: uuidv4(),
      username: userData.username,
      password: hashedPassword,
      email: userData.email,
      role: userData.role || 'user',
      name: userData.name || userData.username,
      createdAt: new Date(),
    };

    users.set(newUser.id, newUser);
    logger.info('用户创建成功', { userId: newUser.id, username: newUser.username });

    const { password, ...userWithoutPassword } = newUser;
    return userWithoutPassword;
  }

  /**
   * 获取所有用户
   * @returns {Array} 用户列表
   */
  static async findAll() {
    const allUsers = [];
    for (const user of users.values()) {
      const { password, ...userWithoutPassword } = user;
      allUsers.push(userWithoutPassword);
    }
    return allUsers;
  }
}

module.exports = {
  User,
  initTestUsers,
};