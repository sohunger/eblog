<#include "/inc/layout.ftlh"/>

<@layout "首页">
    <#include "/inc/panel.ftlh" />
    <div class="layui-container">
        <div class="layui-row layui-col-space15">
            <div class="layui-col-md8">
                <div class="fly-panel" style="margin-bottom: 0;">

                    <div class="fly-panel-title fly-filter">
                        <a href="" class="layui-this">综合</a>
                        <span class="fly-mid"></span>
                        <a href="">未结</a>
                        <span class="fly-mid"></span>
                        <a href="">已结</a>
                        <span class="fly-mid"></span>
                        <a href="">精华</a>
                        <span class="fly-filter-right layui-hide-xs">
            <a href="" class="layui-this">按最新</a>
            <span class="fly-mid"></span>
            <a href="">按热议</a>
          </span>
                    </div>
                    <@posts categoryId = CurrentCategoryId pn=pn size=2>
                        <ul class="fly-list">
                            <#list results.records as post>
                                <@posting post></@posting>
                            </#list>
                        </ul>
                        <@paging results></@paging>
                    </@posts>


                </div>
            </div>
            <#include "/inc/right.ftlh" />
        </div>
    </div>
    <script>
        layui.cache.page = 'jie';
    </script>

</@layout>










