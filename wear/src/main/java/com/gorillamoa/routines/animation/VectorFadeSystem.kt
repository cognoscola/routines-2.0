package com.gorillamoa.routines.animation


import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem

/**
 * We're going to fade the lines from left to right
 */
class VectorFadeSystem(val orientation:Int):SortedIteratingSystem(Family.all(AlphaComponent::class.java, EdgeComponent::class.java).get(),{
    entityA,entityB ->

    //which one has the lowest pt from left to right is the one who will light up first.

    val edgeA = entityA.getComponent(EdgeComponent::class.java)
    val edgeB = entityB.getComponent(EdgeComponent::class.java)

    if (Math.min(edgeA.x1, edgeA.x2) < Math.min(edgeB.x1, edgeB.x2)) {
        -1
    }else if(Math.min(edgeA.x1, edgeA.x2) >= Math.min(edgeB.x1, edgeB.x2)){
        1
    }else {0
    }

}){

    var appearing = true

    fun toggleTransition(){
        appearing = !appearing

    }


    override fun processEntity(entity: Entity?, deltaTime: Float) {

        entity?.getComponent(AlphaComponent::class.java)?.let {

                if (appearing) {
                    it.realDelayTime += deltaTime
                    if (it.realDelayTime < it.delayMillis) {
                        return
                    }
                }else{
                    it.realDelayTime -= deltaTime
                    if (it.realDelayTime > 0) {
                        return
                    }
                }

            entity.getComponent(EdgeComponent::class.java)?.apply {

                if (appearing) {
                    if (it.alpha < 255) {
                        it.alpha += 25
                        it.alpha = Math.min(it.alpha, 255)
                    }

                }else{
                    if (it.alpha >0) {
                        it.alpha -= 25
                        it.alpha = Math.max(it.alpha, 0)
                    }
                }
            }
        }
    }
}

