package com.example.drivermodule.Entity.RoadBook

import android.databinding.ObservableField
import java.io.Serializable


class RoadBookDistanceEntity  :Serializable{


    var title =  ObservableField<String>()

    var distance  = ObservableField<String>()

    var day_count = ObservableField<String>()

    var pointCount = ObservableField<String>()

    var carType =  ObservableField<String>()

    var seasonType = ObservableField<String>()

    var speType = ObservableField<String>()

    var introduce = ObservableField<String>()

    var guideId = ObservableField<String>()

    var id  = ObservableField<String>()

    var day = ObservableField<Int>()

    var visible = ObservableField<Boolean>(false)



    var content =  ObservableField<String>("先说文章的结构，即所要写的这篇记叙文用什么结构来表现出来。它包括这篇文章分几层写，哪些材料先写，哪些后写，哪些详写，哪些略写，如何安排过渡，于何处伏笔，在哪里呼应，如何开头，怎样结尾，等等。从整篇记叙文来看、常见的结构有顺序、倒叙、插叙。顺叙，就是按照事情发生、发展的，过程进行叙述。包括以下几种情况:其一，按时间的推移来叙述;其二，按事情的发展来叙述;其三，按认识发展的过程来叙述;其四，按作者的行踪来叙述。倒叙，就是把事情的结局，或某个突出的精彩片断提到前边写，然后再按事件发生、发展的顺序叙述。倒叙的运用有四种类型:一种是把事件的结局提前，造成悬念，然后再按时间顺序叙述事情的发生与发展;一种是把事件中最精彩的或最紧张的片断截取下来，写在前面，震动和吸引读者，然后按时间的顺序叙述事件的起因、发展与结局;一种是先写眼前的事物，由此及彼，引起回忆，再追叙往事，形成倒叙，一种是先写当前情况，再回忆过去的情况，以形成鲜明的对比，给读者留下深刻印象。插叙，是在文章的叙述中，暂时中断叙述的线索，插入一些与中心事件有关的内容，然后再继续进行原来的叙述。插叙的具体内容和形式有种种不同:有的是追叙，对过去事件片断进行回忆，有的是补叙，对有关人和事作必要的补充、解释;有的是逆叙，对有关内容由近及远、由今及古地回溯，灵活多样的插叙，可以使主题开掘得更深刻，情节展开得更充分，内容表现得更充实，人物形象刻画得更丰满，避免了平辅直叙。")


}