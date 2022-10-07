<div class="layui-col-md4">


    <dl class="fly-panel fly-list-one">
        <dt class="fly-panel-title">本周热议</dt>
        <@hotPosts>
            <#list results as posts>
                <dd>
                    <a href="post/${posts.id}">${posts.title}</a>
                    <span><i class="iconfont icon-pinglun1"></i> ${posts.commentCount}</span>
                </dd>
            </#list>
        </@hotPosts>
    </dl>


</div>