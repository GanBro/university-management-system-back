package com.wubo.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wubo.api.entity.*;
import com.wubo.api.mapper.UniversityAdminMappingMapper;
import com.wubo.api.mapper.UniversityMapper;
import com.wubo.api.mapper.UserMapper;
import com.wubo.api.mapper.UserUniversityFollowMapper;
import com.wubo.api.service.EmailService;
import com.wubo.api.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserUniversityFollowMapper followMapper;
    @Resource
    private UniversityMapper universityMapper;
    @Resource
    private UniversityAdminMappingMapper universityAdminMappingMapper;
    @Resource
    private EmailService emailService;

    private Map<String, PasswordResetInfo> passwordResetCodes = new ConcurrentHashMap<>();

    @Override
    public User login(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());  // 直接比较明文密码
        User existingUser = userMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            // 更新最后登录时间
            existingUser.setLastLogin(new java.sql.Timestamp(System.currentTimeMillis()));
            userMapper.updateById(existingUser);
        }
        return existingUser;
    }

    @Override
    public User getUserByToken(String token) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token", token);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public boolean register(User user) {
        // 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        User existingUser = userMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            // 用户名已存在
            return false;
        }

        // 生成唯一 token
        user.setToken(java.util.UUID.randomUUID().toString());

        // 设置创建时间
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // 设置默认角色为 user
        user.setRole("user");

        // 保存用户
        return userMapper.insert(user) > 0;
    }

    @Override
    public Page<User> getUserList(Page<User> page, Map<String, Object> params) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        String username = (String) params.get("username");
        String email = (String) params.get("email");
        String role = (String) params.get("role");

        if (StringUtils.hasText(username)) {
            queryWrapper.like("username", username);
        }
        if (StringUtils.hasText(email)) {
            queryWrapper.like("email", email);
        }
        if (StringUtils.hasText(role)) {
            queryWrapper.eq("role", role);
        }

        queryWrapper.orderByDesc("user_id");

        // 获取用户列表
        Page<User> userPage = userMapper.selectPage(page, queryWrapper);

        // 处理高校管理员信息
        List<User> records = userPage.getRecords();
        if (records != null && !records.isEmpty()) {
            for (User user : records) {
                if ("university_admin".equals(user.getRole())) {
                    // 获取管理员的高校信息
                    UniversityAdminMapping mapping = universityAdminMappingMapper.selectWithUniversityName(user.getUserId());
                    if (mapping != null) {
                        user.setUniversityId(mapping.getUniversityId());
                        user.setUniversityName(mapping.getUniversityName());
                    }
                }
            }
        }

        return userPage;
    }

    @Override
    public User getUserDetail(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user != null && "university_admin".equals(user.getRole())) {
            // 查询高校管理员映射关系
            UniversityAdminMapping mapping = universityAdminMappingMapper.selectWithUniversityName(userId);
            if (mapping != null) {
                user.setUniversityId(mapping.getUniversityId());
                user.setUniversityName(mapping.getUniversityName());
            }
        }
        return user;
    }

    @Override
    public User getUserById(Integer userId) {
        return userMapper.selectById(userId);
    }

    @Override
    @Transactional
    public boolean updateUser(User user) {
        if (user.getUserId() == null) {
            throw new RuntimeException("用户ID不能为空");
        }

        // 获取当前用户信息
        User existingUser = userMapper.selectById(user.getUserId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 不允许更新用户名
        user.setUsername(null);

        // 如果密码为空，则不更新密码
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword(null);
        }

        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        int result = userMapper.updateById(user);

        // 处理高校管理员映射关系
        if (result > 0) {
            // 首先删除现有的映射关系(无论角色如何)
            LambdaQueryWrapper<UniversityAdminMapping> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UniversityAdminMapping::getUserId, user.getUserId());
            universityAdminMappingMapper.delete(queryWrapper);

            // 如果是高校管理员角色并且提供了大学ID，则创建新的映射关系
            if ("university_admin".equals(user.getRole()) && user.getUniversityId() != null) {
                UniversityAdminMapping mapping = new UniversityAdminMapping();
                mapping.setUserId(user.getUserId());
                mapping.setUniversityId(user.getUniversityId());
                universityAdminMappingMapper.insert(mapping);
            }
        }

        return result > 0;
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 检查用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 如果是高校管理员，删除映射关系
        if ("university_admin".equals(user.getRole())) {
            LambdaQueryWrapper<UniversityAdminMapping> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UniversityAdminMapping::getUserId, id);
            universityAdminMappingMapper.delete(queryWrapper);
        }

        // 删除用户
        int result = userMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除用户失败");
        }
    }

    @Override
    @Transactional
    public void batchDeleteUsers(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("用户ID列表不能为空");
        }

        // 检查是否有管理员账号
        LambdaQueryWrapper<User> adminWrapper = new LambdaQueryWrapper<>();
        adminWrapper.in(User::getUserId, ids)
                .eq(User::getRole, "admin");
        Long adminCount = userMapper.selectCount(adminWrapper);
        if (adminCount > 0) {
            throw new RuntimeException("不能删除管理员账号");
        }

        // 检查用户是否都存在
        LambdaQueryWrapper<User> existWrapper = new LambdaQueryWrapper<>();
        existWrapper.in(User::getUserId, ids);
        Long existingCount = userMapper.selectCount(existWrapper);
        if (existingCount != ids.size()) {
            throw new RuntimeException("部分用户不存在");
        }

        // 查找并删除高校管理员映射关系
        LambdaQueryWrapper<User> adminUserWrapper = new LambdaQueryWrapper<>();
        adminUserWrapper.in(User::getUserId, ids)
                .eq(User::getRole, "university_admin");
        List<User> adminUsers = userMapper.selectList(adminUserWrapper);

        if (!adminUsers.isEmpty()) {
            List<Integer> adminUserIds = adminUsers.stream()
                    .map(User::getUserId)
                    .collect(Collectors.toList());

            LambdaQueryWrapper<UniversityAdminMapping> mappingWrapper = new LambdaQueryWrapper<>();
            mappingWrapper.in(UniversityAdminMapping::getUserId, adminUserIds);
            universityAdminMappingMapper.delete(mappingWrapper);
        }

        // 批量删除用户
        LambdaQueryWrapper<User> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.in(User::getUserId, ids);
        int result = userMapper.delete(deleteWrapper);
        if (result != ids.size()) {
            throw new RuntimeException("批量删除用户失败");
        }
    }

    @Override
    @Transactional
    public boolean createUser(User user) {
        try {
            // 设置创建时间和更新时间
            Timestamp now = new Timestamp(System.currentTimeMillis());
            user.setCreatedAt(now);
            user.setUpdatedAt(now);

            // 插入用户记录
            int result = userMapper.insert(user);

            // 如果是高校管理员，创建映射关系
            if (result > 0 && "university_admin".equals(user.getRole()) && user.getUniversityId() != null) {
                UniversityAdminMapping mapping = new UniversityAdminMapping();
                mapping.setUserId(user.getUserId());
                mapping.setUniversityId(user.getUniversityId());
                universityAdminMappingMapper.insert(mapping);
            }

            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException("创建用户失败: " + e.getMessage(), e);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return null;
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    @Transactional
    public boolean updateProfile(User user) {
        if (user.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 只允许更新邮箱和头像
        User existingUser = userMapper.selectById(user.getUserId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        if (StringUtils.hasText(user.getEmail())) {
            existingUser.setEmail(user.getEmail());
        }

        if (StringUtils.hasText(user.getAvatar())) {
            existingUser.setAvatar(user.getAvatar());
        }

        existingUser.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return userMapper.updateById(existingUser) > 0;
    }

    @Override
    @Transactional
    public boolean updatePassword(UpdatePasswordRequest request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        // 获取当前用户
        User user = userMapper.selectById(request.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!user.getPassword().equals(request.getOldPassword())) {
            throw new RuntimeException("当前密码错误");
        }

        // 更新密码
        user.setPassword(request.getNewPassword());
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        return userMapper.updateById(user) > 0;
    }

    @Override
    public void followUniversity(Integer userId, Integer universityId) {
        LambdaQueryWrapper<UserUniversityFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserUniversityFollow::getUserId, userId)
                .eq(UserUniversityFollow::getUniversityId, universityId);

        if(followMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("已关注该高校");
        }

        UserUniversityFollow follow = new UserUniversityFollow();
        follow.setUserId(userId);
        follow.setUniversityId(universityId);
        follow.setCreatedAt(LocalDateTime.now());
        followMapper.insert(follow);
    }

    @Override
    public void unfollowUniversity(Integer userId, Integer universityId) {
        LambdaQueryWrapper<UserUniversityFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserUniversityFollow::getUserId, userId)
                .eq(UserUniversityFollow::getUniversityId, universityId);
        followMapper.delete(wrapper);
    }

    @Override
    public List<University> getFollowedUniversities(Integer userId) {
        LambdaQueryWrapper<UserUniversityFollow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserUniversityFollow::getUserId, userId);
        List<UserUniversityFollow> follows = followMapper.selectList(wrapper);

        if(follows.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> ids = follows.stream()
                .map(UserUniversityFollow::getUniversityId)
                .collect(Collectors.toList());

        return universityMapper.selectBatchIds(ids);
    }

    private static class PasswordResetInfo {
        private String code;
        private String email;
        private LocalDateTime expireTime;
        private String resetToken;

        public PasswordResetInfo(String code, String email) {
            this.code = code;
            this.email = email;
            this.expireTime = LocalDateTime.now().plusMinutes(15); // 15分钟有效期
        }

        public boolean isValid(String inputCode, String inputEmail) {
            return code.equals(inputCode) && email.equals(inputEmail) && LocalDateTime.now().isBefore(expireTime);
        }

        public String generateResetToken() {
            resetToken = UUID.randomUUID().toString();
            expireTime = LocalDateTime.now().plusMinutes(30); // 重置令牌30分钟有效期
            return resetToken;
        }

        public boolean isValidResetToken(String token) {
            return resetToken != null && resetToken.equals(token) && LocalDateTime.now().isBefore(expireTime);
        }
    }

    @Override
    public boolean sendPasswordResetCode(String username, String email) {
        // 查找用户
        User user = getUserByUsername(username);
        if (user == null || !email.equals(user.getEmail())) {
            return false;
        }

        // 生成6位数字验证码
        String code = String.format("%06d", new Random().nextInt(999999));

        // 存储验证码信息
        passwordResetCodes.put(username, new PasswordResetInfo(code, email));

        // 通过邮件服务发送验证码
        boolean sent = emailService.sendVerificationCode(email, code);

        if (sent) {
            log.info("密码重置验证码已发送至邮箱: {}, 验证码: {}", email, code);
        } else {
            log.error("密码重置验证码发送失败, 邮箱: {}", email);
        }

        return sent;
    }

    @Override
    public String verifyPasswordResetCode(String username, String email, String code) {
        PasswordResetInfo resetInfo = passwordResetCodes.get(username);

        if (resetInfo != null && resetInfo.isValid(code, email)) {
            // 生成重置令牌
            return resetInfo.generateResetToken();
        }

        return null;
    }

    @Override
    public boolean resetPassword(String username, String resetToken, String newPassword) {
        PasswordResetInfo resetInfo = passwordResetCodes.get(username);

        if (resetInfo == null || !resetInfo.isValidResetToken(resetToken)) {
            return false;
        }

        // 更新用户密码
        User user = getUserByUsername(username);
        if (user == null) {
            return false;
        }

        user.setPassword(newPassword);
        user.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        int result = userMapper.updateById(user);

        if (result > 0) {
            // 密码更新成功后，移除重置信息
            passwordResetCodes.remove(username);
            return true;
        }

        return false;
    }
}
