/**
 * JWT工具单元测试
 */
const jwt = require('jsonwebtoken');
const config = require('../src/config');
const jwtUtils = require('../src/utils/jwt');

describe('JWT工具测试', () => {
  const testPayload = {
    userId: 'test-user-id',
    username: 'testuser',
    role: 'user',
  };

  describe('generateToken', () => {
    test('应该成功生成Token', () => {
      const token = jwtUtils.generateToken(testPayload);

      expect(token).toBeDefined();
      expect(typeof token).toBe('string');
      expect(token.split('.').length).toBe(3); // JWT格式：header.payload.signature
    });

    test('生成的Token应该包含正确的载荷', () => {
      const token = jwtUtils.generateToken(testPayload);
      const decoded = jwt.verify(token, config.jwt.secret);

      expect(decoded.userId).toBe(testPayload.userId);
      expect(decoded.username).toBe(testPayload.username);
      expect(decoded.role).toBe(testPayload.role);
      expect(decoded.exp).toBeDefined();
      expect(decoded.iat).toBeDefined();
    });
  });

  describe('verifyToken', () => {
    test('应该成功验证有效Token', () => {
      const token = jwtUtils.generateToken(testPayload);
      const decoded = jwtUtils.verifyToken(token);

      expect(decoded).not.toBeNull();
      expect(decoded.userId).toBe(testPayload.userId);
      expect(decoded.username).toBe(testPayload.username);
    });

    test('应该拒绝无效Token', () => {
      const decoded = jwtUtils.verifyToken('invalid-token');

      expect(decoded).toBeNull();
    });

    test('应该拒绝格式错误的Token', () => {
      const decoded = jwtUtils.verifyToken('not.a.valid.jwt');

      expect(decoded).toBeNull();
    });

    test('应该拒绝用错误密钥签名的Token', () => {
      const token = jwt.sign(testPayload, 'wrong-secret', { expiresIn: '1h' });
      const decoded = jwtUtils.verifyToken(token);

      expect(decoded).toBeNull();
    });
  });

  describe('decodeToken', () => {
    test('应该成功解析Token', () => {
      const token = jwtUtils.generateToken(testPayload);
      const decoded = jwtUtils.decodeToken(token);

      expect(decoded).not.toBeNull();
      expect(decoded.userId).toBe(testPayload.userId);
    });

    test('应该解析无效Token（不验证签名）', () => {
      const token = jwtUtils.generateToken(testPayload);
      // 修改Token使其签名无效
      const parts = token.split('.');
      const tamperedToken = `${parts[0]}.${parts[1]}.invalidsignature`;

      const decoded = jwtUtils.decodeToken(tamperedToken);
      expect(decoded).not.toBeNull();
      expect(decoded.userId).toBe(testPayload.userId);
    });
  });

  describe('getTokenExpiration', () => {
    test('应该返回Token过期时间', () => {
      const token = jwtUtils.generateToken(testPayload);
      const expiration = jwtUtils.getTokenExpiration(token);

      expect(expiration).toBeInstanceOf(Date);
      expect(expiration.getTime()).toBeGreaterThan(Date.now());
    });

    test('应该返回null对于无效Token', () => {
      const expiration = jwtUtils.getTokenExpiration('invalid-token');

      expect(expiration).toBeNull();
    });
  });
});