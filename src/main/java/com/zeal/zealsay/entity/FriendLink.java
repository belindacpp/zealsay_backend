package com.zeal.zealsay.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 友链记录表
 * </p>
 *
 * @author zhanglei
 * @since 2019-07-31
 */
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class FriendLink {

    /**
     * 主键.
     */
    @TableId(value = "id", type = IdType.ID_WORKER)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 头像.
     */
    private String avatar;

    /**
     * 友链名称.
     */
    private String friendName;

    /**
     * 友链介绍.
     */
    private String friendInfo;

    /**
     * 链接地址.
     */
    private String link;

    /**
     * 印象颜色.
     */
    private String color;

    /**
     * 创建时间.
     *
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;


}