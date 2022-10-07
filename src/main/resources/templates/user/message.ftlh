<#include "/inc/layout.ftlh"/>

<@layout "我的消息">
    <div class="layui-container fly-marginTop fly-user-main">
        <@centerLeft level=3></@centerLeft>

        <div class="site-tree-mobile layui-hide">
            <i class="layui-icon">&#xe602;</i>
        </div>
        <div class="site-mobile-shade"></div>

        <div class="site-tree-mobile layui-hide">
            <i class="layui-icon">&#xe602;</i>
        </div>
        <div class="site-mobile-shade"></div>


        <div class="fly-panel fly-panel-user" pad20>
            <div class="layui-tab layui-tab-brief" lay-filter="user" id="LAY_msg" style="margin-top: 15px;">
                <button class="layui-btn layui-btn-danger" id="LAY_delallmsg">清空全部消息</button>
                <div id="LAY_minemsg" style="margin-top: 10px;">
                    <!--<div class="fly-none">您暂时没有最新消息</div>-->
                    <ul class="mine-msg">
                        <#list pageData.records as data>
                            <li data-id="${data.id}">
                                <blockquote class="layui-elem-quote">

                                    <#if data.type == 0>
                                        系统消息：${data.content}
                                    </#if>
                                    <#if data.type == 1>
                                        ${data.fromUserName} 评论了你的文章 <${data.postTitle}>，内容是 (${data.content})
                                    </#if>
                                    <#--                  <#if data.type == 2>-->
                                    <#--                    ${data.fromUserName} 回复了你的评论 (${data.commentReply})，文章是 <${data.postTitle}>-->
                                    <#--                  </#if>-->
                                </blockquote>
                                <p><span>${timeAgo(data.created)}</span><a href="javascript:;"
                                                                           class="layui-btn layui-btn-small layui-btn-danger fly-delete"
                                                                           alert="true">删除</a></p>
                            </li>
                        </#list>
                    </ul>
                    <@paging pageData></@paging>
                </div>
            </div>
        </div>

    </div>
    <script>
        layui.cache.page = 'user';
    </script>
</@layout>

