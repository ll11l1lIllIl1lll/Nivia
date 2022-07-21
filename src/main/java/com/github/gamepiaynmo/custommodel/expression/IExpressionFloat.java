package com.github.gamepiaynmo.custommodel.expression;

import com.github.gamepiaynmo.custommodel.client.render.RenderContext;

public interface IExpressionFloat extends IExpression {
   float eval(RenderContext context);

   default ExpressionType getExpressionType() {
      return ExpressionType.FLOAT;
   }
}
