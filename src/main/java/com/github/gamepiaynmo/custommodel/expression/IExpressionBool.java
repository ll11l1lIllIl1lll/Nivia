package com.github.gamepiaynmo.custommodel.expression;

import com.github.gamepiaynmo.custommodel.client.render.RenderContext;

public interface IExpressionBool extends IExpression {
   boolean eval(RenderContext context);

   default ExpressionType getExpressionType() {
      return ExpressionType.BOOL;
   }
}
