package com.example.model

enum class BackgroundStyle {
    INK_WASH_GOLD,
    MISTY_JADE,
    MIDNIGHT_INDIGO,
    IMPERIAL_RED,
    SCHOLAR_BROWN
}

enum class DifferenceType {
    QING_OFFICIAL_HAT,  // 修改顶戴花翎或流苏颜色
    PALACE_LANTERN,     // 灯笼明暗 or 流苏缺失
    CLASSIC_FAN,        // 扇面花纹变化 or 消失
    BRONZE_BELL,        // 编钟挂钩 or 钟锤缺失
    INK_BUTTERFLY,      // 墨蝶振翅 or 颜色变幻
    SOARING_CRANE,      // 仙鹤飞翔 or 翅膀缺失
    TEA_CUP,            // 茶杯热气 or 杯壁图案
    SCROLL_BOOK,        // 画卷题字 or 轴部卷起
    FLOWER_LOTUS,       // 荷花绽放 or 荷叶增减
    ANCIENT_COIN,       // 铜钱孔型 or 铜钱缺失
    SPLASH_FISH,        // 锦鲤跃水 or 水花波纹
    CLOUDS,             // 祥云飘动 or 祥云形态
    TREE_BRANCH,        // 垂柳树叶 or 松针变化
    PAGODA,             // 古塔顶尖 or 塔窗变化
    INCENSE_BURNER      // 炉香袅袅 or 香炉雕饰
}

data class DifferenceDefinition(
    val id: Int,
    val name: String,
    val x: Float,       // 0f to 1f relative layout coords
    val y: Float,       // 0f to 1f relative layout coords
    val radius: Float = 0.08f,
    val type: DifferenceType
)

data class LevelDefinition(
    val id: Int,
    val idiom: String,
    val pinyin: String,
    val story: String,
    val backgroundType: BackgroundStyle,
    val differences: List<DifferenceDefinition>
)

object LevelDefinitions {
    val levels = listOf(
        LevelDefinition(
            id = 1,
            idiom = "走马观花",
            pinyin = "zǒu mǎ guān huā",
            story = "走马：骑着跑马。骑在奔跑的马上看花。形容得意、轻率、不深入地观察事物。",
            backgroundType = BackgroundStyle.INK_WASH_GOLD,
            differences = listOf(
                DifferenceDefinition(1, "官帽花翎变化", 0.35f, 0.40f, 0.08f, DifferenceType.QING_OFFICIAL_HAT),
                DifferenceDefinition(2, "折扇穗子缺失", 0.70f, 0.65f, 0.08f, DifferenceType.CLASSIC_FAN),
                DifferenceDefinition(3, "空中飞舞墨蝶", 0.50f, 0.18f, 0.08f, DifferenceType.INK_BUTTERFLY),
                DifferenceDefinition(4, "祥云缺少一朵", 0.85f, 0.22f, 0.08f, DifferenceType.CLOUDS)
            )
        ),
        LevelDefinition(
            id = 2,
            idiom = "对牛弹琴",
            pinyin = "duì niú tán qín",
            story = "比喻对愚蠢的人讲深奥的道理。含有徒劳无功、白费口舌的讽刺之意。",
            backgroundType = BackgroundStyle.MISTY_JADE,
            differences = listOf(
                DifferenceDefinition(1, "茶杯热气缭绕", 0.25f, 0.70f, 0.08f, DifferenceType.TEA_CUP),
                DifferenceDefinition(2, "松针繁茂变化", 0.78f, 0.30f, 0.08f, DifferenceType.TREE_BRANCH),
                DifferenceDefinition(3, "古乐谱字画卷", 0.45f, 0.52f, 0.08f, DifferenceType.SCROLL_BOOK),
                DifferenceDefinition(4, "荷池荷花含苞", 0.88f, 0.72f, 0.08f, DifferenceType.FLOWER_LOTUS)
            )
        ),
        LevelDefinition(
            id = 3,
            idiom = "掩耳盗铃",
            pinyin = "yǎn ěr dào líng",
            story = "捂住自己的耳朵去偷铃铛。比喻明明自己欺骗自己，认为别人不知道。",
            backgroundType = BackgroundStyle.MIDNIGHT_INDIGO,
            differences = listOf(
                DifferenceDefinition(1, "屋檐金铎编钟", 0.55f, 0.25f, 0.08f, DifferenceType.BRONZE_BELL),
                DifferenceDefinition(2, "夜游墨蝶幻色", 0.22f, 0.35f, 0.08f, DifferenceType.INK_BUTTERFLY),
                DifferenceDefinition(3, "地面遗落铜钱", 0.38f, 0.82f, 0.08f, DifferenceType.ANCIENT_COIN),
                DifferenceDefinition(4, "长褂官带绳结", 0.68f, 0.60f, 0.08f, DifferenceType.QING_OFFICIAL_HAT)
            )
        ),
        LevelDefinition(
            id = 4,
            idiom = "纸上谈兵",
            pinyin = "zhǐ shàng tán bīng",
            story = "比喻空谈理论，不能解决实际问题。源于战国名将赵括空谈兵法导致大败的故事。",
            backgroundType = BackgroundStyle.SCHOLAR_BROWN,
            differences = listOf(
                DifferenceDefinition(1, "案头香炉飘烟", 0.65f, 0.42f, 0.08f, DifferenceType.INCENSE_BURNER),
                DifferenceDefinition(2, "兵法画卷印章", 0.30f, 0.58f, 0.08f, DifferenceType.SCROLL_BOOK),
                DifferenceDefinition(3, "宫廷红灯流苏", 0.82f, 0.20f, 0.08f, DifferenceType.PALACE_LANTERN),
                DifferenceDefinition(4, "窗外斑驳竹影", 0.18f, 0.28f, 0.08f, DifferenceType.TREE_BRANCH)
            )
        ),
        LevelDefinition(
            id = 5,
            idiom = "名落孙山",
            pinyin = "míng luò sūn shān",
            story = "指考试没有考中，名字排在孙山之后。比喻投考未中或选拔落选。",
            backgroundType = BackgroundStyle.IMPERIAL_RED,
            differences = listOf(
                DifferenceDefinition(1, "皇榜红帕变化", 0.50f, 0.32f, 0.08f, DifferenceType.SCROLL_BOOK),
                DifferenceDefinition(2, "金榜仙鹤图案", 0.85f, 0.55f, 0.08f, DifferenceType.SOARING_CRANE),
                DifferenceDefinition(3, "赶考折扇墨画", 0.28f, 0.72f, 0.08f, DifferenceType.CLASSIC_FAN),
                DifferenceDefinition(4, "腰间翡翠铜钱", 0.34f, 0.88f, 0.08f, DifferenceType.ANCIENT_COIN)
            )
        ),
        LevelDefinition(
            id = 6,
            idiom = "铁杵磨针",
            pinyin = "tiě chǔ mó zhēn",
            story = "比喻只要有毅力，肯下苦功，再难的事情也能成功，如同磨铁杵成针。",
            backgroundType = BackgroundStyle.MISTY_JADE,
            differences = listOf(
                DifferenceDefinition(1, "老妪额头抹额", 0.48f, 0.50f, 0.08f, DifferenceType.QING_OFFICIAL_HAT),
                DifferenceDefinition(2, "木盆锦鲤游动", 0.24f, 0.75f, 0.08f, DifferenceType.SPLASH_FISH),
                DifferenceDefinition(3, "山庙古塔剪影", 0.80f, 0.25f, 0.08f, DifferenceType.PAGODA),
                DifferenceDefinition(4, "竹林中落叶片", 0.72f, 0.55f, 0.08f, DifferenceType.TREE_BRANCH)
            )
        ),
        LevelDefinition(
            id = 7,
            idiom = "叶公好龙",
            pinyin = "yè gōng hào lóng",
            story = "比喻口头上声称爱好某事物，实际上并不真正喜爱甚至感到畏惧。",
            backgroundType = BackgroundStyle.IMPERIAL_RED,
            differences = listOf(
                DifferenceDefinition(1, "屏风金龙独角", 0.58f, 0.35f, 0.08f, DifferenceType.SOARING_CRANE),
                DifferenceDefinition(2, "铜香炉三脚柱", 0.26f, 0.62f, 0.08f, DifferenceType.INCENSE_BURNER),
                DifferenceDefinition(3, "窗棂宫挂红灯", 0.15f, 0.20f, 0.08f, DifferenceType.PALACE_LANTERN),
                DifferenceDefinition(4, "叶公腰佩玉璧", 0.76f, 0.78f, 0.08f, DifferenceType.ANCIENT_COIN)
            )
        ),
        LevelDefinition(
            id = 8,
            idiom = "井底之蛙",
            pinyin = "jǐng dǐ zhī wā",
            story = "井底的青蛙。比喻见识狭隘、目光短浅且自以为是的人。",
            backgroundType = BackgroundStyle.MISTY_JADE,
            differences = listOf(
                DifferenceDefinition(1, "水面青蛙鼓囊", 0.42f, 0.68f, 0.08f, DifferenceType.TEA_CUP),
                DifferenceDefinition(2, "枯井缝生绿苔", 0.15f, 0.45f, 0.08f, DifferenceType.TREE_BRANCH),
                DifferenceDefinition(3, "井口仙鹤飞过", 0.65f, 0.18f, 0.08f, DifferenceType.SOARING_CRANE),
                DifferenceDefinition(4, "井底落入铜板", 0.58f, 0.85f, 0.08f, DifferenceType.ANCIENT_COIN)
            )
        ),
        LevelDefinition(
            id = 9,
            idiom = "画龙点睛",
            pinyin = "huà lóng diǎn jīng",
            story = "原形容梁代画家张僧繇画龙点上眼睛龙即飞去。后比喻说话写文章在关键处增添内容使之传神。",
            backgroundType = BackgroundStyle.INK_WASH_GOLD,
            differences = listOf(
                DifferenceDefinition(1, "壁画金龙明眸", 0.52f, 0.30f, 0.08f, DifferenceType.SOARING_CRANE),
                DifferenceDefinition(2, "朱红砚台玉台", 0.32f, 0.72f, 0.08f, DifferenceType.SCROLL_BOOK),
                DifferenceDefinition(3, "画师官帽红顶", 0.78f, 0.60f, 0.08f, DifferenceType.QING_OFFICIAL_HAT),
                DifferenceDefinition(4, "大殿宫灯摇曳", 0.88f, 0.18f, 0.08f, DifferenceType.PALACE_LANTERN)
            )
        ),
        LevelDefinition(
            id = 10,
            idiom = "守株待兔",
            pinyin = "shǒu zhū dài tù",
            story = "比喻妄想不经过努力而侥幸得到成功；也比喻死守狭隘经验坚守木讷。",
            backgroundType = BackgroundStyle.SCHOLAR_BROWN,
            differences = listOf(
                DifferenceDefinition(1, "农夫倒卧斗笠", 0.60f, 0.72f, 0.08f, DifferenceType.QING_OFFICIAL_HAT),
                DifferenceDefinition(2, "枯树桩旁幼兔", 0.35f, 0.65f, 0.08f, DifferenceType.INK_BUTTERFLY),
                DifferenceDefinition(3, "大树丫鸟巢", 0.28f, 0.22f, 0.08f, DifferenceType.TREE_BRANCH),
                DifferenceDefinition(4, "远山古塔高崇", 0.85f, 0.38f, 0.08f, DifferenceType.PAGODA)
            )
        ),
        LevelDefinition(
            id = 11,
            idiom = "刻舟求剑",
            pinyin = "kè zhōu qiú jiàn",
            story = "在开动的船上刻记号去寻找落水的剑。比喻死守教条，拘泥成法，不懂得随着情势变化而变化。",
            backgroundType = BackgroundStyle.MISTY_JADE,
            differences = listOf(
                DifferenceDefinition(1, "画卷中的舟楫", 0.50f, 0.58f, 0.08f, DifferenceType.SCROLL_BOOK),
                DifferenceDefinition(2, "船舷剑刻痕迹", 0.62f, 0.66f, 0.08f, DifferenceType.ANCIENT_COIN),
                DifferenceDefinition(3, "江面浮沉鱼影", 0.25f, 0.78f, 0.08f, DifferenceType.SPLASH_FISH),
                DifferenceDefinition(4, "芦苇丛烟云飘", 0.80f, 0.42f, 0.08f, DifferenceType.CLOUDS)
            )
        ),
        LevelDefinition(
            id = 12,
            idiom = "闻鸡起舞",
            pinyin = "wén jī qǐ wǔ",
            story = "听到鸡鸣就起床舞剑。形容有志报国的人及时奋起努力，刻苦锻炼。",
            backgroundType = BackgroundStyle.MIDNIGHT_INDIGO,
            differences = listOf(
                DifferenceDefinition(1, "屋脊报晓金鸡", 0.78f, 0.30f, 0.08f, DifferenceType.SOARING_CRANE),
                DifferenceDefinition(2, "舞剑人宝剑穗", 0.44f, 0.68f, 0.08f, DifferenceType.CLASSIC_FAN),
                DifferenceDefinition(3, "庭院红灯高挂", 0.18f, 0.26f, 0.08f, DifferenceType.PALACE_LANTERN),
                DifferenceDefinition(4, "地面练剑石锁", 0.28f, 0.85f, 0.08f, DifferenceType.TEA_CUP)
            )
        ),
        LevelDefinition(
            id = 13,
            idiom = "完璧归赵",
            pinyin = "wán bì guī zhào",
            story = "比喻把原物原封不动地归还给原主。出自战国蔺相如将和氏璧完好带回赵国的故事。",
            backgroundType = BackgroundStyle.IMPERIAL_RED,
            differences = listOf(
                DifferenceDefinition(1, "座上和氏璧环", 0.48f, 0.45f, 0.08f, DifferenceType.ANCIENT_COIN),
                DifferenceDefinition(2, "殿中九鼎龙纹", 0.22f, 0.70f, 0.08f, DifferenceType.INCENSE_BURNER),
                DifferenceDefinition(3, "朝臣官带绣样", 0.72f, 0.62f, 0.08f, DifferenceType.QING_OFFICIAL_HAT),
                DifferenceDefinition(4, "蟠龙石柱云纹", 0.82f, 0.30f, 0.08f, DifferenceType.CLOUDS)
            )
        ),
        LevelDefinition(
            id = 14,
            idiom = "自相矛盾",
            pinyin = "zì xiāng máo dùn",
            story = "比喻自己的言行前后不一、相互抵触。源自楚国商人同时夸赞自己的长矛和圆盾。",
            backgroundType = BackgroundStyle.SCHOLAR_BROWN,
            differences = listOf(
                DifferenceDefinition(1, "大汉左手长矛", 0.38f, 0.50f, 0.08f, DifferenceType.TREE_BRANCH),
                DifferenceDefinition(2, "右手环形盾牌", 0.64f, 0.58f, 0.08f, DifferenceType.CLASSIC_FAN),
                DifferenceDefinition(3, "大汉腰间佩刀", 0.50f, 0.78f, 0.08f, DifferenceType.ANCIENT_COIN),
                DifferenceDefinition(4, "路人官服朝珠", 0.82f, 0.68f, 0.08f, DifferenceType.QING_OFFICIAL_HAT)
            )
        ),
        LevelDefinition(
            id = 15,
            idiom = "四面楚歌",
            pinyin = "sì miàn chǔ gē",
            story = "比喻陷入四面受敌、孤立无援的绝境。出自秦末项羽被刘邦楚军围困垓下的典故。",
            backgroundType = BackgroundStyle.MIDNIGHT_INDIGO,
            differences = listOf(
                DifferenceDefinition(1, "营帐外篝火烈", 0.35f, 0.72f, 0.08f, DifferenceType.INCENSE_BURNER),
                DifferenceDefinition(2, "马背上的雕弓", 0.75f, 0.62f, 0.08f, DifferenceType.CLASSIC_FAN),
                DifferenceDefinition(3, "夜空寒月满缺", 0.15f, 0.18f, 0.08f, DifferenceType.CLOUDS),
                DifferenceDefinition(4, "帐前飘扬旌旗", 0.52f, 0.35f, 0.08f, DifferenceType.TREE_BRANCH)
            )
        ),
        LevelDefinition(
            id = 16,
            idiom = "画饼充饥",
            pinyin = "huà bǐng chōng jī",
            story = "画个饼来解除饥饿。比喻用空想来安慰自己，并不解决实际问题。",
            backgroundType = BackgroundStyle.SCHOLAR_BROWN,
            differences = listOf(
                DifferenceDefinition(1, "画板上的面饼", 0.52f, 0.48f, 0.08f, DifferenceType.ANCIENT_COIN),
                DifferenceDefinition(2, "案桌细瓷笔筒", 0.28f, 0.65f, 0.08f, DifferenceType.TEA_CUP),
                DifferenceDefinition(3, "书生手里毛笔", 0.70f, 0.60f, 0.08f, DifferenceType.QING_OFFICIAL_HAT),
                DifferenceDefinition(4, "墙上挂幅古卷", 0.85f, 0.28f, 0.08f, DifferenceType.SCROLL_BOOK)
            )
        ),
        LevelDefinition(
            id = 17,
            idiom = "名列前茅",
            pinyin = "míng liè qián máo",
            story = "指名次排在最前面。茅：古代行军在前探路的士兵手持的红色茅草旗帜。",
            backgroundType = BackgroundStyle.IMPERIAL_RED,
            differences = listOf(
                DifferenceDefinition(1, "红校官红缨枪", 0.36f, 0.45f, 0.08f, DifferenceType.TREE_BRANCH),
                DifferenceDefinition(2, "举牌手铜腰牌", 0.65f, 0.75f, 0.08f, DifferenceType.ANCIENT_COIN),
                DifferenceDefinition(3, "公堂屏风仙鹤", 0.50f, 0.28f, 0.08f, DifferenceType.SOARING_CRANE),
                DifferenceDefinition(4, "主考官头顶冠", 0.82f, 0.50f, 0.08f, DifferenceType.QING_OFFICIAL_HAT)
            )
        ),
        LevelDefinition(
            id = 18,
            idiom = "柳暗花明",
            pinyin = "lǐu àn huā míng",
            story = "绿柳成荫，鲜花灿烂。比喻在遇到困难、极其绝望时，突然出现转机，看到希望的曙光。",
            backgroundType = BackgroundStyle.MISTY_JADE,
            differences = listOf(
                DifferenceDefinition(1, "桥头杨柳垂绦", 0.24f, 0.35f, 0.08f, DifferenceType.TREE_BRANCH),
                DifferenceDefinition(2, "溪畔红杏花丛", 0.78f, 0.70f, 0.08f, DifferenceType.FLOWER_LOTUS),
                DifferenceDefinition(3, "溪面石跳桥板", 0.50f, 0.75f, 0.08f, DifferenceType.TEA_CUP),
                DifferenceDefinition(4, "水阁酒旗卷风", 0.85f, 0.32f, 0.08f, DifferenceType.SCROLL_BOOK)
            )
        ),
        LevelDefinition(
            id = 19,
            idiom = "大智若愚",
            pinyin = "dà zhì ruò yú",
            story = "才智极高的人，表面上看起来好像很愚笨。形容不炫耀显露自己的才华才学。",
            backgroundType = BackgroundStyle.SCHOLAR_BROWN,
            differences = listOf(
                DifferenceDefinition(1, "案头香炉双耳", 0.62f, 0.60f, 0.08f, DifferenceType.INCENSE_BURNER),
                DifferenceDefinition(2, "书箧古籍卷帙", 0.26f, 0.48f, 0.08f, DifferenceType.SCROLL_BOOK),
                DifferenceDefinition(3, "桌上红釉茶具", 0.48f, 0.72f, 0.08f, DifferenceType.TEA_CUP),
                DifferenceDefinition(4, "窗棂古雅红灯", 0.82f, 0.22f, 0.08f, DifferenceType.PALACE_LANTERN)
            )
        ),
        LevelDefinition(
            id = 20,
            idiom = "一鸣惊人",
            pinyin = "yī míng jīng rén",
            story = "比喻平时没有突出的表现，一旦做起事来，却暴露出惊人的才华或做出惊人的成绩。",
            backgroundType = BackgroundStyle.INK_WASH_GOLD,
            differences = listOf(
                DifferenceDefinition(1, "云际翱翔仙鹤", 0.45f, 0.25f, 0.08f, DifferenceType.SOARING_CRANE),
                DifferenceDefinition(2, "松干松鼠抱果", 0.24f, 0.52f, 0.08f, DifferenceType.TREE_BRANCH),
                DifferenceDefinition(3, "峰顶古刹小塔", 0.80f, 0.40f, 0.08f, DifferenceType.PAGODA),
                DifferenceDefinition(4, "竹林中浮水雁", 0.65f, 0.78f, 0.08f, DifferenceType.SPLASH_FISH)
            )
        )
    )
}
