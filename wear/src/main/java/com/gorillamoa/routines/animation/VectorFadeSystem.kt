package com.gorillamoa.routines.animation

import android.util.Log
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.gorillamoa.routines.utils.*
import com.gorillamoa.routines.views.LivingBackground


/**
 * We're going to fade the lines from left to right
 */
class VectorFadeSystem:SortedIteratingSystem(Family.all(AlphaComponent::class.java, EdgeComponent::class.java).get(),{
    entityA,entityB ->


    //which one has the lowest pt from left to right is the one who will light up first.

    val edgeA = entityA.getComponent(EdgeComponent::class.java).edgeNode.itself
    val edgeB = entityB.getComponent(EdgeComponent::class.java).edgeNode.itself

    if (Math.min(edgeA.a.x, edgeA.b.x) < Math.min(edgeB.a.x, edgeB.b.x)) {
        NEG_ONE_INT
    }else if(Math.min(edgeA.a.x, edgeA.b.x) >= Math.min(edgeB.a.x, edgeB.b.x)){
        ONE_INT
    }else {
        ZERO_INT
    }

}){

    @Suppress("unused")
    private val tag:String = VectorFadeSystem::class.java.name
    var appearing = true

    fun toggleTransition(){
        appearing = !appearing
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {

        val alphaComponenet = entity?.getComponent(AlphaComponent::class.java)
        alphaComponenet?.let {

                if (appearing) {
                    it.realDelayTime += deltaTime
                    if (it.realDelayTime < it.delaySecond) {
                        return
                    }else{
                        it.realDelayTime = it.delaySecond.toFloat()
                    }
                }else{
                    it.realDelayTime -= deltaTime
                    if (it.realDelayTime > ZERO_FLOAT) {
                        return
                    }else{
                        it.realDelayTime = ZERO_FLOAT
                    }
                  }

            entity.getComponent(EdgeComponent::class.java)?.apply {

                if (appearing) {
                    if (it.alpha < TWOFIFTYFIVE) {
                        it.alpha += FORTY_FIVE_INT
                        it.alpha = Math.min(it.alpha, TWOFIFTYFIVE)

                        if ((it.alpha == TWOFIFTYFIVE) and (!edgeNode.latch)) {
                            edgeNode.latch = true

                            shouldTriangleLightUpGiven(edgeNode).let {
                                //light up the triangle
                                Log.d("$tag processEntity","Light up Triangle")

                            }
                        }
                    }

                }else{
                    if (it.alpha > ZERO_INT) {
                        it.alpha -= FORTY_FIVE_INT
                        it.alpha = Math.max(it.alpha, ZERO_INT)
                    }
                }
            }
        }
    }

    fun shouldTriangleLightUpGiven(edgeNode: LivingBackground.EdgeNode):Boolean{
        //check the edge's neighbours
        return shouldLightUp(edgeNode.neighbour).or(shouldLightUp(edgeNode.parent))
    }

    fun isEdgeNodeLit(edgeNode: LivingBackground.EdgeNode?):Boolean{
        return edgeNode?.latch?:true
    }

    fun shouldLightUp(triangleNode:LivingBackground.TriangleNode?):Boolean{

        return triangleNode?.let {
            if (!it.latch) {
                if (isEdgeNodeLit(it.edgeNodeAB) and isEdgeNodeLit(it.edgeNodeAC) and isEdgeNodeLit(it.edgeNodeBC)) {
                    it.latch = true
                }
            }
            it.latch
        }?:false
    }
}

